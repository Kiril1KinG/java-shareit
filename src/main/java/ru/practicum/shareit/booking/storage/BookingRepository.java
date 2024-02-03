package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Entity.BookingEntity;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
}
