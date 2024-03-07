package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.classBuilder.TestBookingProvider;
import ru.practicum.shareit.classBuilder.TestItemProvider;
import ru.practicum.shareit.classBuilder.TestUserProvider;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private UserEntity itemOwner;
    private UserEntity booker;
    private ItemEntity item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

        itemOwner = TestUserProvider.buildUserEntity(null, "itemOwner", "owner@yandex.ru");
        itemOwner = userRepository.save(itemOwner);
        booker = TestUserProvider.buildUserEntity(null, "booker", "booker@yandex.ru");
        booker = userRepository.save(booker);

        item = TestItemProvider.provideItemEntity(null, "Дрель", "Проводная дрель", true, itemOwner, null);
        item = itemRepository.save(item);
    }

    @Test
    void existsBookingByItemIdAndBookerIdAndStatus() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertTrue(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(item.getId(), booker.getId(), BookingStatus.WAITING));
        assertFalse(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(item.getId(), booker.getId(), BookingStatus.REJECTED));
    }

    @Test
    void findAllByBookerId() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByBookerId(booker.getId(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndEndIsBefore() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByBookerIdAndEndIsBefore(booker.getId(), LocalDateTime.now().plusDays(10),
                        Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(booker.getId(), LocalDateTime.now(),
                        LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndStartIsAfter() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking), bookingRepository.findAllByBookerIdAndStartIsAfter(booker.getId(), LocalDateTime.now(),
                Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerId() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerId(itemOwner.getId(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(itemOwner.getId(),
                        LocalDateTime.now(), LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBefore() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndEndIsBefore(itemOwner.getId(),
                        LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfter() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndStartIsAfter(itemOwner.getId(),
                        LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndStatus(itemOwner.getId(), BookingStatus.WAITING, Pageable.ofSize(10)).getContent());
    }

    @Test
    void existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore() {
        BookingEntity booking = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertTrue(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(item.getId(),
                booker.getId(), BookingStatus.WAITING, LocalDateTime.now()));
        assertFalse(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(item.getId(),
                booker.getId(), BookingStatus.REJECTED, LocalDateTime.now()));
    }

    @Test
    void findLastAndNextBookingByItemId() {
        BookingEntity last = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED);
        bookingRepository.save(last);
        BookingEntity next = TestBookingProvider.provideBookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);
        bookingRepository.save(next);

        assertEquals(List.of(last, next),
                bookingRepository.findLastAndNextBookingByItemId(item.getId(), LocalDateTime.now()));
    }
}