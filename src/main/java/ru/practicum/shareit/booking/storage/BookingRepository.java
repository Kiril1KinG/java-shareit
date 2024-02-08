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

    Collection<BookingEntity> findAllByBooker_IdOrderByStartDesc(Integer bookerId);

    Collection<BookingEntity> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);

    Collection<BookingEntity> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Integer bookerId,
                                                                                              LocalDateTime start,
                                                                                              LocalDateTime now);

    Collection<BookingEntity> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(Integer bookerId,
                                                                                LocalDateTime now);

    Collection<BookingEntity> findAllByBooker_IdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status);

    Collection<BookingEntity> findAllByItem_Owner_IdOrderByStartDesc(Integer ownerId);

    Collection<BookingEntity> findAllByItem_Owner_IdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Integer ownerId,
                                                                                                  LocalDateTime start,
                                                                                                  LocalDateTime now);

    Collection<BookingEntity> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Integer ownerId, LocalDateTime now);

    Collection<BookingEntity> findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Integer ownerId,
                                                                                    LocalDateTime now);

    Collection<BookingEntity> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Integer ownerId,
                                                                              BookingStatus bookingStatus);

    Boolean existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(Integer itemId, Integer userId,
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
