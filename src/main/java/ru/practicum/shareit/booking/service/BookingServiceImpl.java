package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.exception.RepeatedRequestException;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    private static BookingState filterBookingState(String state) {
        if (state == null) {
            return BookingState.ALL;
        }
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException("Unknown state: " + state);
        }
    }

    private static void checkBookingRequestTime(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new TimeValidationException("Incorrect time, end can not be before start");
        }
        if (booking.getEnd().equals(booking.getStart())) {
            throw new TimeValidationException("Incorrect time, end can not be equal start");
        }
    }

    @Override
    public Booking add(Booking booking) {
        checkBookingRequestTime(booking);
        ItemEntity itemEntity = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Add booking failed, item with id %d not exists", booking.getItem().getId())));
        if (!itemEntity.getAvailable()) {
            throw new NotAvailableException(
                    String.format("Add booking failed, item with id %d not available now", itemEntity.getId()));
        }

        UserEntity userEntity = userRepository.findById(booking.getBooker().getId()).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Add booking failed, user with id %d not exists", booking.getBooker().getId())));

        if (itemEntity.getOwner().getId().equals(userEntity.getId())) {
            throw new DataDoesNotExistsException(
                    String.format("Add booking failed, user with id %d is owner", booking.getBooker().getId()));
        }
        if (bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(itemEntity.getId(),
                booking.getBooker().getId(), BookingStatus.WAITING)) {
            throw new DataAlreadyExistsException("Add booking failed, booking request already exists");
        }

        BookingEntity res = bookingRepository.save(mapper.toBookingEntity(booking));
        res.setItem(itemEntity);
        res.setBooker(userEntity);
        log.info("Booking added: {}", res);
        return mapper.toBooking(res);
    }

    @Override
    public Booking approveBooking(Integer bookingId, Integer userId, boolean approve) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Approve booking failed, booking with id %d not exists", bookingId)));

        if (bookingEntity.getStatus().equals(BookingStatus.APPROVED) ||
                bookingEntity.getStatus().equals(BookingStatus.REJECTED)) {
            throw new RepeatedRequestException("Approve booking failed, booking already approved");
        }

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Approve booking failed, user with id %d not exists", userId)));

        if (!bookingEntity.getItem().getOwner().getId().equals(userEntity.getId())) {
            throw new NotOwnerException(
                    String.format("Approve booking failed, user with id %d not owner", userId));
        }

        if (approve) {
            bookingEntity.setStatus(BookingStatus.APPROVED);
        } else {
            bookingEntity.setStatus(BookingStatus.REJECTED);
        }

        return mapper.toBooking(bookingRepository.save(bookingEntity));
    }

    @Override
    public Booking getById(Integer bookingId, Integer userId) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Get booking by id failed, booking with id %d not exists", bookingId)));

        ItemEntity item = bookingEntity.getItem();
        if (!item.getOwner().getId().equals(userId) &&
                !bookingEntity.getBooker().getId().equals(userId)) {
            throw new NotOwnerException("Get booking failed, you not owner/booker");
        }
        log.info("Booking by id received: {}", bookingEntity);
        return mapper.toBooking(bookingEntity);
    }

    @Override
    public Collection<Booking> getAllBookingsByState(Integer userId, String bookingState, Integer from, Integer size) {
        BookingState state = filterBookingState(bookingState);
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Get all booking by state failed, user with id %d not exists", userId));
        }
        Pageable pageable = getPageable(from, size, Sort.by("start").descending());
        Collection<BookingEntity> res = new ArrayList<>();
        switch (state) {
            case ALL:
                res = bookingRepository.findAllByBookerId(userId, pageable).getContent();
                break;
            case CURRENT:
                res = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), pageable).getContent();
                break;
            case PAST:
                res = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable).getContent();
                break;
            case FUTURE:
                res = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable).getContent();
                break;
            case WAITING:
                res = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable).getContent();
                break;
            case REJECTED:
                res = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable).getContent();
                break;
        }
        return res.stream()
                .map(mapper::toBooking)
                .collect(Collectors.toList());
    }

    public Collection<Booking> getAllBookingsForItemsByState(Integer userId, String bookingState, Integer from, Integer size) {
        BookingState state = filterBookingState(bookingState);
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Get all booking by state failed, user with id %d not exists", userId));
        }
        Pageable pageable = getPageable(from, size, Sort.by("start").descending());
        Collection<BookingEntity> res = new ArrayList<>();
        switch (state) {
            case ALL:
                res = bookingRepository.findAllByItemOwnerId(userId, pageable).getContent();
                break;
            case CURRENT:
                res = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(userId, LocalDateTime.now(),
                        LocalDateTime.now(), pageable).getContent();
                break;
            case PAST:
                res = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable).getContent();
                break;
            case FUTURE:
                res = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable).getContent();
                break;
            case WAITING:
                res = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable).getContent();
                break;
            case REJECTED:
                res = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable).getContent();
                break;
        }
        return res.stream()
                .map(mapper::toBooking)
                .collect(Collectors.toList());
    }

    private Pageable getPageable(Integer from, Integer size, Sort sort) {
        if ((from == null && size != null) || (size == null && from != null)) {
            throw new PaginationParamsException("Get bookings failed, one of pagination params cannot be null");
        }
        if (from != null && size != null) {
            return PageRequest.of(from / size, size, sort);
        } else {
            int count = (int) bookingRepository.count();
            return PageRequest.of(0, count > 0 ? count : 1, sort);
        }
    }
}