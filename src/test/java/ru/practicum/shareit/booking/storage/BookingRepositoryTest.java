package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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

        itemOwner = new UserEntity(null, "itemOwner", "owner@yandex.ru");
        itemOwner = userRepository.save(itemOwner);
        booker = new UserEntity(null, "booker", "booker@yandex.ru");
        booker = userRepository.save(booker);

        item = new ItemEntity(null, "Дрель", "Проводная дрель", true, itemOwner, null);
        item = itemRepository.save(item);
    }

    @Test
    void existsBookingByItemIdAndBookerIdAndStatus() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertTrue(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(item.getId(), booker.getId(), BookingStatus.WAITING));
        Assertions.assertFalse(bookingRepository.existsBookingByItemIdAndBookerIdAndStatus(item.getId(), booker.getId(), BookingStatus.REJECTED));
    }

    @Test
    void findAllByBookerId() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByBookerId(booker.getId(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndEndIsBefore() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByBookerIdAndEndIsBefore(booker.getId(), LocalDateTime.now().plusDays(10),
                        Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(booker.getId(), LocalDateTime.now(),
                        LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndStartIsAfter() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByBookerIdAndStartIsAfter(booker.getId(), LocalDateTime.now(),
                        Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerId() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerId(itemOwner.getId(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(itemOwner.getId(),
                        LocalDateTime.now(), LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBefore() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndEndIsBefore(itemOwner.getId(),
                        LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfter() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndStartIsAfter(itemOwner.getId(),
                        LocalDateTime.now(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertEquals(List.of(booking),
                bookingRepository.findAllByItemOwnerIdAndStatus(itemOwner.getId(), BookingStatus.WAITING, Pageable.ofSize(10)).getContent());
    }

    @Test
    void existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore() {
        BookingEntity booking = new BookingEntity(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        Assertions.assertTrue(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(item.getId(),
                booker.getId(), BookingStatus.WAITING, LocalDateTime.now()));
        Assertions.assertFalse(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(item.getId(),
                booker.getId(), BookingStatus.REJECTED, LocalDateTime.now()));
    }

    @Test
    void findLastAndNextBookingByItemId() {
        BookingEntity last = new BookingEntity(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED);
        bookingRepository.save(last);
        BookingEntity next = new BookingEntity(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);
        bookingRepository.save(next);

        Assertions.assertEquals(List.of(last, next),
                bookingRepository.findLastAndNextBookingByItemId(item.getId(), LocalDateTime.now()));
    }
}