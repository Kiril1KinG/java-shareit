package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

    boolean existsBookingByItemIdAndBookerIdAndStatus(Integer itemId, Integer bookerId, BookingStatus bookingStatus);

    Collection<BookingEntity> findAllByBookerIdOrderByStartDesc(Integer bookerId);

    Collection<BookingEntity> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);

    Collection<BookingEntity> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Integer bookerId,
                                                                                             LocalDateTime start,
                                                                                             LocalDateTime now);

    Collection<BookingEntity> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Integer bookerId,
                                                                               LocalDateTime now);

    Collection<BookingEntity> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status);

    Collection<BookingEntity> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId);

    Collection<BookingEntity> findAllByItemOwnerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Integer ownerId,
                                                                                                LocalDateTime start,
                                                                                                LocalDateTime now);

    Collection<BookingEntity> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Integer ownerId, LocalDateTime now);

    Collection<BookingEntity> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Integer ownerId,
                                                                                  LocalDateTime now);

    Collection<BookingEntity> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId,
                                                                            BookingStatus bookingStatus);

    Boolean existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(Integer itemId, Integer userId,
                                                                    BookingStatus bookingStatus, LocalDateTime now);

    @Query(value = "SELECT * FROM bookings AS b " +
            "WHERE item_id = :itemId AND start_date < :now AND status = 'APPROVED' ORDER BY end_date DESC limit 1",
            nativeQuery = true)
    BookingEntity findLastBookingByItemId(@Param("itemId") Integer itemId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM bookings AS b " +
            "WHERE item_id = :itemId AND start_date > :now AND status = 'APPROVED' ORDER BY start_date ASC limit 1",
            nativeQuery = true)
    BookingEntity findNextBookingByItemId(@Param("itemId") Integer itemId, @Param("now") LocalDateTime now);
}