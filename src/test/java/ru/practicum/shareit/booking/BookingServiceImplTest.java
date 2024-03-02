package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.classBuilder.BookingBuilder;
import ru.practicum.shareit.classBuilder.ItemBuilder;
import ru.practicum.shareit.classBuilder.UserBuilder;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.exception.RepeatedRequestException;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper);

    }

    @Test
    void add() {
        Booking bookingWithIncorrectItemId = BookingBuilder.buildBooking(1,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(99, "item", "desc", true, new User(), null, null, null, null),
                UserBuilder.buildUser(1, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithUnavailableItem = BookingBuilder.buildBooking(2,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(2, "item", "desc", false, new User(), null, null, null, null),
                UserBuilder.buildUser(2, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithIncorrectUserId = BookingBuilder.buildBooking(3,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(3, "item", "desc", true, new User(), null, null, null, null),
                UserBuilder.buildUser(99, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingFromOwner = BookingBuilder.buildBooking(4,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(4, "item", "desc", true, UserBuilder.buildUser(4, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(4, "user", "email"),
                BookingStatus.WAITING);

        Booking repeatedBooking = BookingBuilder.buildBooking(5,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(5, "item", "desc", true, UserBuilder.buildUser(1, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(5, "user", "email"),
                BookingStatus.WAITING);

        Booking booking = BookingBuilder.buildBooking(6,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(6, "item", "desc", true, UserBuilder.buildUser(1, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(6, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithIncorrectTime = BookingBuilder.buildBooking(7,
                LocalDateTime.of(2023, 1, 21, 20, 30),
                LocalDateTime.of(2023, 1, 20, 20, 30),
                ItemBuilder.buildItem(7, "item", "desc", true, new User(), null, null, null, null),
                UserBuilder.buildUser(7, "user", "email"),
                BookingStatus.WAITING);


        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        when(itemRepository.findById(2)).thenReturn(
                Optional.of(itemMapper.toItemEntity(bookingWithUnavailableItem.getItem())));

        when(itemRepository.findById(3)).thenReturn(
                Optional.of(itemMapper.toItemEntity(bookingWithIncorrectUserId.getItem())));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        when(itemRepository.findById(4)).thenReturn(
                Optional.of(itemMapper.toItemEntity(bookingFromOwner.getItem())));
        when(userRepository.findById(4)).thenReturn(
                Optional.of(userMapper.toUserEntity(bookingFromOwner.getBooker())));

        when(itemRepository.findById(5)).thenReturn(
                Optional.of(itemMapper.toItemEntity(repeatedBooking.getItem())));
        when(userRepository.findById(5)).thenReturn(
                Optional.of(userMapper.toUserEntity(repeatedBooking.getBooker())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(5, 5, BookingStatus.WAITING))
                .thenReturn(true);

        when(itemRepository.findById(6)).thenReturn(
                Optional.of(itemMapper.toItemEntity(booking.getItem())));
        when(userRepository.findById(6)).thenReturn(
                Optional.of(userMapper.toUserEntity(booking.getBooker())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(6, 6, BookingStatus.WAITING))
                .thenReturn(false);
        when(bookingRepository.save(bookingMapper.toBookingEntity(booking)))
                .thenReturn(bookingMapper.toBookingEntity(booking));


        assertThrows(DataDoesNotExistsException.class, () -> bookingService.add(bookingWithIncorrectItemId));
        verify(bookingRepository, never()).save(any());

        assertThrows(NotAvailableException.class, () -> bookingService.add(bookingWithUnavailableItem));
        verify(bookingRepository, never()).save(any());

        assertThrows(DataDoesNotExistsException.class, () -> bookingService.add(bookingWithIncorrectUserId));
        verify(bookingRepository, never()).save(any());

        assertThrows(DataDoesNotExistsException.class, () -> bookingService.add(bookingFromOwner));
        verify(bookingRepository, never()).save(any());

        assertThrows(DataAlreadyExistsException.class, () -> bookingService.add(repeatedBooking));
        verify(bookingRepository, never()).save(any());

        assertEquals(booking, bookingService.add(booking));
        verify(bookingRepository, times(1)).save(any());

        assertThrows(TimeValidationException.class, () -> bookingService.add(bookingWithIncorrectTime));
        verify(bookingRepository, never()).save(bookingMapper.toBookingEntity(bookingWithIncorrectTime));
    }

    @Test
    void approveBooking() {
        Booking alreadyApprovedBooking = BookingBuilder.buildBooking(1,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(1, "item", "desc", true, UserBuilder.buildUser(2, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(1, "user", "email"),
                BookingStatus.APPROVED);

        Booking bookingWithIncorrectUserId = BookingBuilder.buildBooking(2,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(2, "item", "desc", true, UserBuilder.buildUser(1, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(99, "user", "email"),
                BookingStatus.WAITING);

        Booking bookingWithIncorrectOwner = BookingBuilder.buildBooking(3,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(3, "item", "desc", true, UserBuilder.buildUser(1, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(3, "user", "email"),
                BookingStatus.WAITING);

        Booking booking = BookingBuilder.buildBooking(4,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(4, "item", "desc", true, UserBuilder.buildUser(4, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(4, "user", "email"),
                BookingStatus.WAITING);


        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        when(bookingRepository.findById(1)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(alreadyApprovedBooking)));

        when(bookingRepository.findById(2)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(bookingWithIncorrectUserId)));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        when(bookingRepository.findById(3)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(bookingWithIncorrectUserId)));
        when(userRepository.findById(3)).thenReturn(
                Optional.of(userMapper.toUserEntity(bookingWithIncorrectOwner.getBooker())));

        when(bookingRepository.findById(4)).thenReturn(
                Optional.of(bookingMapper.toBookingEntity(booking)));
        when(userRepository.findById(4)).thenReturn(
                Optional.of(userMapper.toUserEntity(booking.getBooker())));
        when(bookingRepository.save(bookingMapper.toBookingEntity(booking))).thenReturn(
                bookingMapper.toBookingEntity(booking));


        assertThrows(DataDoesNotExistsException.class, () -> bookingService.approveBooking(99, 1, true));
        verify(bookingRepository, never()).save(any());

        assertThrows(RepeatedRequestException.class, () -> bookingService.approveBooking(1, 1, true));
        verify(bookingRepository, never()).save(any());

        assertThrows(DataDoesNotExistsException.class, () -> bookingService.approveBooking(2, 99, true));
        verify(bookingRepository, never()).save(any());

        assertThrows(NotOwnerException.class, () -> bookingService.approveBooking(3, 3, true));
        verify(bookingRepository, never()).save(any());

        assertEquals(booking, bookingService.approveBooking(4, 4, true));
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void getById() {
        Booking booking = BookingBuilder.buildBooking(1,
                LocalDateTime.of(2023, 1, 20, 20, 30),
                LocalDateTime.of(2023, 1, 21, 20, 30),
                ItemBuilder.buildItem(1, "item", "desc", true, UserBuilder.buildUser(2, "user", "email"), null, null, null, null),
                UserBuilder.buildUser(1, "user", "email"),
                BookingStatus.WAITING);


        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        when(bookingRepository.findById(1)).thenReturn(Optional.of(bookingMapper.toBookingEntity(booking)));


        assertThrows(DataDoesNotExistsException.class, () -> bookingService.getById(99, 1));

        assertThrows(NotOwnerException.class, () -> bookingService.getById(1, 99));

        assertEquals(booking, bookingService.getById(1, 1));
    }

    @Test
    void getAllBookingsByState() {
        Page<BookingEntity> page = new PageImpl<>(Collections.emptyList());


        when(userRepository.existsById(99)).thenReturn(false);

        when(userRepository.existsById(1)).thenReturn(true);

        when(bookingRepository.findAllByBookerId(eq(1), any())).thenReturn(page);

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(eq(1), any(),
                any(), any())).thenReturn(page);

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(eq(1), any(),
                any())).thenReturn(page);

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(eq(1), any(),
                any())).thenReturn(page);

        when(bookingRepository.findAllByBookerIdAndStatus(eq(1), eq(BookingStatus.WAITING),
                any())).thenReturn(page);

        when(bookingRepository.findAllByBookerIdAndStatus(eq(1), eq(BookingStatus.REJECTED),
                any())).thenReturn(page);


        assertThrows(UnknownStateException.class, () -> bookingService.getAllBookingsByState(1, "STATE", 0, 1));

        assertThrows(DataDoesNotExistsException.class, () -> bookingService.getAllBookingsByState(99, "All", 0, 1));
        verify(bookingRepository, never()).findAllByItemOwnerId(any(), any());

        assertThrows(PaginationParamsException.class, () -> bookingService.getAllBookingsByState(1, null, 0, null));
        verify(bookingRepository, never()).findAllByItemOwnerId(any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "ALL", 0, 1));
        verify(bookingRepository, times(1)).findAllByBookerId(any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "CURRENT", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "PAST", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndIsBefore(any(), any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "FUTURE", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartIsAfter(any(), any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "WAITING", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(any(), eq(BookingStatus.WAITING), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsByState(1, "REJECTED", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(any(), eq(BookingStatus.REJECTED), any());

    }

    @Test
    void getAllBookingsForItemsByState() {
        Page<BookingEntity> page = new PageImpl<>(Collections.emptyList());


        when(userRepository.existsById(99)).thenReturn(false);

        when(userRepository.existsById(1)).thenReturn(true);

        when(bookingRepository.findAllByItemOwnerId(any(), any())).thenReturn(page);

        when(bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(any(), any(),
                any(), any())).thenReturn(page);

        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(any(), any(),
                any())).thenReturn(page);

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(any(), any(),
                any())).thenReturn(page);

        when(bookingRepository.findAllByItemOwnerIdAndStatus(any(), eq(BookingStatus.WAITING),
                any())).thenReturn(page);

        when(bookingRepository.findAllByItemOwnerIdAndStatus(any(), eq(BookingStatus.REJECTED),
                any())).thenReturn(page);


        assertThrows(DataDoesNotExistsException.class, () -> bookingService.getAllBookingsForItemsByState(99, "ALL", 0, 1));
        verify(bookingRepository, never()).findAllByItemOwnerId(any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "ALL", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerId(any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "CURRENT", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(any(), any(), any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "PAST", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndIsBefore(any(), any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "FUTURE", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartIsAfter(any(), any(), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "WAITING", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(any(), eq(BookingStatus.WAITING), any());

        assertEquals(Collections.emptyList(), bookingService.getAllBookingsForItemsByState(1, "REJECTED", 0, 1));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(any(), eq(BookingStatus.WAITING), any());
    }
}