package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;
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

    @Override
    public Booking add(Booking booking) {
        checkBookingRequestTime(booking);
        Optional<ItemEntity> item = itemRepository.findById(booking.getItem().getId());
        if (item.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Add booking failed, item with id %d not exists", booking.getItem().getId()));
        }
        if (!item.get().getAvailable()) {
            throw new NotAvailableException(
                    String.format("Add booking failed, item with id %d not available now", item.get().getId()));
        }
        Optional<UserEntity> user = userRepository.findById(booking.getBooker().getId());
        if (user.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Add booking failed, user with id %d not exists", booking.getBooker().getId()));
        }
        if (item.get().getOwner().getId().equals(user.get().getId())) {
            throw new DataDoesNotExistsException(
                    String.format("Add booking failed, user with id %d owner", booking.getBooker().getId()));
        }
        if (bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(item.get().getId(),
                booking.getBooker().getId(), BookingStatus.WAITING)) {
            throw new DataAlreadyExistsException("Add booking failed, booking request already exists");
        }
        BookingEntity res = bookingRepository.save(mapper.toBookingEntity(booking));
        res.setItem(item.get());
        res.setBooker(user.get());
        log.info("Booking added: {}", res);
        return mapper.toBooking(res);
    }

    @Override
    public Booking approveBooking(Integer bookingId, Integer userId, boolean approve) {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(bookingId);
        if (bookingEntity.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Approve booking failed, booking with id %d not exists", bookingId));
        }
        if (bookingEntity.get().getStatus().equals(BookingStatus.APPROVED) ||
                bookingEntity.get().getStatus().equals(BookingStatus.REJECTED)) {
            throw new RepeatedRequestException("Approve booking failed, booking already approved");
        }
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Approve booking failed, user with id %d not exists", userId));
        }
        if (!bookingEntity.get().getItem().getOwner().getId().equals(userEntity.get().getId())) {
            throw new NotOwnerException(
                    String.format("Approve booking failed, user with id %d not owner", userId));
        }
        if (approve) {
            bookingEntity.get().setStatus(BookingStatus.APPROVED);
        } else {
            bookingEntity.get().setStatus(BookingStatus.REJECTED);
        }
        return mapper.toBooking(bookingRepository.save(bookingEntity.get()));
    }

    @Override
    public Booking getById(Integer bookingId, Integer userId) {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(bookingId);
        if (bookingEntity.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Get booking by id failed, booking with id %d not exists", bookingId));
        }
        ItemEntity item = bookingEntity.get().getItem();
        if (!item.getOwner().getId().equals(userId) &&
                !bookingEntity.get().getBooker().getId().equals(userId)) {
            throw new NotOwnerException("Get booking failed, you not owner/booker");
        }
        log.info("Booking by id received: {}", bookingEntity.get());
        return mapper.toBooking(bookingEntity.get());
    }

    @Override
    public Collection<Booking> getAllBookingsByState(Integer userId, String bookingState) {
        BookingState state = filterBookingState(bookingState);
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Get all booking by state failed, user with id %d not exists", userId));
        }
        Collection<BookingEntity> res = new ArrayList<>();
        switch (state) {
            case ALL:
                res = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                res = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                res = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                res = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                res = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                res = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
        }
        return res.stream()
                .map(mapper::toBooking)
                .collect(Collectors.toList());
    }

    public Collection<Booking> getAllBookingsForItemsByState(Integer userId, String bookingState) {
        BookingState state = filterBookingState(bookingState);
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Get all booking by state failed, user with id %d not exists", userId));
        }
        Collection<BookingEntity> res = new ArrayList<>();
        switch (state) {
            case ALL:
                res = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                res = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                res = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                res = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                res = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                res = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
        }
        return res.stream()
                .map(mapper::toBooking)
                .collect(Collectors.toList());
    }

    private void checkBookingRequestTime(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new TimeValidationException("Incorrect time, end can not be before start");
        }
        if (booking.getEnd().equals(booking.getStart())) {
            throw new TimeValidationException("Incorrect time, end can not be equal start");
        }
    }
}