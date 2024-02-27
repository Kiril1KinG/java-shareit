package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.exception.RepeatedRequestException;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

class BookingServiceImplTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper);

    }

    @Test
    void add() {
        Booking bookingWithIncorrectItemId = new Booking(1,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(99, "item", "desc", true, new User(), null, null, null, null),
                new User(1, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithUnavailableItem = new Booking(2,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(2, "item", "desc", false, new User(), null, null, null, null),
                new User(2, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithIncorrectUserId = new Booking(3,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(3, "item", "desc", true, new User(), null, null, null, null),
                new User(99, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingFromOwner = new Booking(4,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(4, "item", "desc", true, new User(4, "user", "email"), null, null, null, null),
                new User(4, "user", "email"),
                BookingStatus.WAITING);

        Booking repeatedBooking = new Booking(5,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(5, "item", "desc", true, new User(1, "user", "email"), null, null, null, null),
                new User(5, "user", "email"),
                BookingStatus.WAITING);

        Booking booking = new Booking(6,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(6, "item", "desc", true, new User(1, "user", "email"), null, null, null, null),
                new User(6, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithIncorrectTime = new Booking(7,
                LocalDateTime.of(2023, 1, 21, 20, 30),
                LocalDateTime.of(2023, 1, 20, 20, 30),
                new Item(7, "item", "desc", true, new User(), null, null, null, null),
                new User(7, "user", "email"),
                BookingStatus.WAITING);


        Mockito.when(itemRepository.findById(99)).thenReturn(Optional.empty());

        Mockito.when(itemRepository.findById(2)).thenReturn(
                Optional.of(itemMapper.toItemEntity(bookingWithUnavailableItem.getItem())));

        Mockito.when(itemRepository.findById(3)).thenReturn(
                Optional.of(itemMapper.toItemEntity(bookingWithIncorrectUserId.getItem())));
        Mockito.when(userRepository.findById(99)).thenReturn(Optional.empty());

        Mockito.when(itemRepository.findById(4)).thenReturn(
                Optional.of(itemMapper.toItemEntity(bookingFromOwner.getItem())));
        Mockito.when(userRepository.findById(4)).thenReturn(
                Optional.of(userMapper.toUserEntity(bookingFromOwner.getBooker())));

        Mockito.when(itemRepository.findById(5)).thenReturn(
                Optional.of(itemMapper.toItemEntity(repeatedBooking.getItem())));
        Mockito.when(userRepository.findById(5)).thenReturn(
                Optional.of(userMapper.toUserEntity(repeatedBooking.getBooker())));
        Mockito.when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(5, 5, BookingStatus.WAITING))
                .thenReturn(true);

        Mockito.when(itemRepository.findById(6)).thenReturn(
                Optional.of(itemMapper.toItemEntity(booking.getItem())));
        Mockito.when(userRepository.findById(6)).thenReturn(
                Optional.of(userMapper.toUserEntity(booking.getBooker())));
        Mockito.when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(6, 6, BookingStatus.WAITING))
                .thenReturn(false);
        Mockito.when(bookingRepository.save(bookingMapper.toBookingEntity(booking)))
                .thenReturn(bookingMapper.toBookingEntity(booking));


        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.add(bookingWithIncorrectItemId));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThrows(NotAvailableException.class, () -> bookingService.add(bookingWithUnavailableItem));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.add(bookingWithIncorrectUserId));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.add(bookingFromOwner));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThrows(DataAlreadyExistsException.class, () -> bookingService.add(repeatedBooking));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertEquals(booking, bookingService.add(booking));
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any());

        Assertions.assertThrows(TimeValidationException.class, () -> bookingService.add(bookingWithIncorrectTime));
        Mockito.verify(bookingRepository, Mockito.never()).save(bookingMapper.toBookingEntity(bookingWithIncorrectTime));
    }

    @Test
    void approveBooking() {
        Booking alreadyApprovedBooking = new Booking(1,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(1, "item", "desc", true, new User(2, "user", "email"), null, null, null, null),
                new User(1, "user", "email"),
                BookingStatus.APPROVED);

        Booking bookingWithIncorrectUserId = new Booking(2,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(2, "item", "desc", true, new User(1, "user", "email"), null, null, null, null),
                new User(99, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithIncorrectOwner = new Booking(3,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(3, "item", "desc", true, new User(1, "user", "email"), null, null, null, null),
                new User(3, "user", "email"),
                BookingStatus.WAITING);

        Booking booking = new Booking(4,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(4, "item", "desc", true, new User(4, "user", "email"), null, null, null, null),
                new User(4, "user", "email"),
                BookingStatus.WAITING);


        Mockito.when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        Mockito.when(bookingRepository.findById(1)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(alreadyApprovedBooking)));

        Mockito.when(bookingRepository.findById(2)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(bookingWithIncorrectUserId)));
        Mockito.when(userRepository.findById(99)).thenReturn(Optional.empty());

        Mockito.when(bookingRepository.findById(3)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(bookingWithIncorrectUserId)));
        Mockito.when(userRepository.findById(3)).thenReturn(
                Optional.of(userMapper.toUserEntity(bookingWithIncorrectOwner.getBooker())));

        Mockito.when(bookingRepository.findById(4)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(booking)));
        Mockito.when(userRepository.findById(4)).thenReturn(
                Optional.of(userMapper.toUserEntity(booking.getBooker())));
        Mockito.when(bookingRepository.save(bookingMapper.toBookingEntity(booking))).thenReturn(
                bookingMapper.toBookingEntity(booking));


        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.approveBooking(99, 1, true));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThrows(RepeatedRequestException.class, () -> bookingService.approveBooking(1, 1, true));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.approveBooking(2, 99, true));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertThrows(NotOwnerException.class, () -> bookingService.approveBooking(3, 3, true));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());

        Assertions.assertEquals(booking, bookingService.approveBooking(4, 4, true));
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void getById() {
        Booking booking = new Booking(1,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                new Item(1, "item", "desc", true, new User(2, "user", "email"), null, null, null, null),
                new User(1, "user", "email"),
                BookingStatus.WAITING);


        Mockito.when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        Mockito.when(bookingRepository.findById(1)).thenReturn(Optional.of(bookingMapper.toBookingEntity(booking)));


        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.getById(99, 1));

        Assertions.assertThrows(NotOwnerException.class, () -> bookingService.getById(1, 99));

        Assertions.assertEquals(booking, bookingService.getById(1, 1));
    }

    @Test
    void getAllBookingsByState() {
        Page<BookingEntity> page = new PageImpl<>(Collections.emptyList());


        Mockito.when(userRepository.existsById(99)).thenReturn(false);

        Mockito.when(userRepository.existsById(1)).thenReturn(true);

        Mockito.when(bookingRepository.findAllByBookerId(Mockito.eq(1), Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Mockito.eq(1), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByBookerIdAndEndIsBefore(Mockito.eq(1), Mockito.any(),
                Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByBookerIdAndStartIsAfter(Mockito.eq(1), Mockito.any(),
                Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.eq(1), Mockito.eq(BookingStatus.WAITING),
                Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.eq(1), Mockito.eq(BookingStatus.REJECTED),
                Mockito.any())).thenReturn(page);


        Assertions.assertThrows(UnknownStateException.class, () -> bookingService.getAllBookingsByState(1, "STATE", 0, 1));

        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.getAllBookingsByState(99, "All", 0, 1));
        Mockito.verify(bookingRepository, Mockito.never()).findAllByItemOwnerId(Mockito.any(), Mockito.any());

        Assertions.assertThrows(PaginationParamsException.class, () -> bookingService.getAllBookingsByState(1, null, 0, null));
        Mockito.verify(bookingRepository, Mockito.never()).findAllByItemOwnerId(Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "ALL", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByBookerId(Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "CURRENT", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "PAST", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndEndIsBefore(Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "FUTURE", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStartIsAfter(Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "WAITING", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(Mockito.any(), Mockito.eq(BookingStatus.WAITING), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "REJECTED", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(Mockito.any(), Mockito.eq(BookingStatus.REJECTED), Mockito.any());

    }

    @Test
    void getAllBookingsForItemsByState() {
        Page<BookingEntity> page = new PageImpl<>(Collections.emptyList());


        Mockito.when(userRepository.existsById(99)).thenReturn(false);

        Mockito.when(userRepository.existsById(1)).thenReturn(true);

        Mockito.when(bookingRepository.findAllByItemOwnerId(Mockito.any(), Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.any(), Mockito.eq(BookingStatus.WAITING),
                Mockito.any())).thenReturn(page);

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.any(), Mockito.eq(BookingStatus.REJECTED),
                Mockito.any())).thenReturn(page);


        Assertions.assertThrows(DataDoesNotExistsException.class, () -> bookingService.getAllBookingsForItemsByState(99, "ALL", 0, 1));
        Mockito.verify(bookingRepository, Mockito.never()).findAllByItemOwnerId(Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "ALL", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerId(Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "CURRENT", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "PAST", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsBefore(Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "FUTURE", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStartIsAfter(Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "WAITING", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(Mockito.any(), Mockito.eq(BookingStatus.WAITING), Mockito.any());

        Assertions.assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "REJECTED", 0, 1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(Mockito.any(), Mockito.eq(BookingStatus.WAITING), Mockito.any());
    }
}