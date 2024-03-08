package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

    boolean existsBookingByItemIdAndBookerIdAndStatus(Integer itemId, Integer bookerId, BookingStatus bookingStatus);

    Page<BookingEntity> findAllByBookerId(Integer bookerId, Pageable pageable);

    Page<BookingEntity> findAllByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime now, Pageable pageable);

    Page<BookingEntity> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime start,
                                                                       LocalDateTime now, Pageable pageable);

    Page<BookingEntity> findAllByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime now, Pageable pageable);

    Page<BookingEntity> findAllByBookerIdAndStatus(Integer bookerId, BookingStatus status, Pageable pageable);

    Page<BookingEntity> findAllByItemOwnerId(Integer ownerId, Pageable pageable);

    Page<BookingEntity> findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(Integer ownerId, LocalDateTime start,
                                                                          LocalDateTime now, Pageable pageable);

    Page<BookingEntity> findAllByItemOwnerIdAndEndIsBefore(Integer ownerId, LocalDateTime now, Pageable pageable);

    Page<BookingEntity> findAllByItemOwnerIdAndStartIsAfter(Integer ownerId, LocalDateTime now, Pageable pageable);

    Page<BookingEntity> findAllByItemOwnerIdAndStatus(Integer ownerId, BookingStatus bookingStatus, Pageable pageable);

    Boolean existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(Integer itemId, Integer userId,
                                                                    BookingStatus bookingStatus, LocalDateTime now);

    @Query(value = "SELECT * FROM (SELECT * FROM bookings AS b " +
            "WHERE item_id = :itemId AND start_date < :now AND status = 'APPROVED' ORDER BY end_date DESC limit 1) " +
            "UNION ALL " +
            "(SELECT * FROM bookings AS b " +
            "WHERE item_id = :itemId AND start_date > :now AND status = 'APPROVED' ORDER BY start_date ASC limit 1)",
            nativeQuery = true)
    Collection<BookingEntity> findLastAndNextBookingByItemId(@Param("itemId") Integer itemId, @Param("now") LocalDateTime now);
}