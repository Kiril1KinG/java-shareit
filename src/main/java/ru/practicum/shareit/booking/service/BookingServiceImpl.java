package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.DataNotExistsException;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;

    @Override
    public Booking add(Booking booking) {
        Booking res = bookingStorage.add(booking);
        log.info("Booking added: {}", res);
        return res;
    }

    @Override
    public Booking get(int id) {
        if (!bookingStorage.contains(id)) {
            throw new DataNotExistsException(
                    String.format("Get booking by id filed, booking with id %d not exists", id));
        }
        Booking booking = bookingStorage.get(id);
        log.info("Booking received: {}", booking);
        return bookingStorage.get(id);
    }

    @Override
    public Booking update(Booking booking) {
        if (!bookingStorage.contains(booking.getId())) {
            throw new DataNotExistsException(
                    String.format("Update booking by id filed, booking with id %d not exists", booking.getId()));
        }
        Booking modified = bookingStorage.get(booking.getId());
        modified.setEnd(booking.getEnd());
        modified.setStatus(booking.getStatus());
        bookingStorage.update(modified);
        log.info("Booking updated: {}", modified);
        return modified;
    }

    @Override
    public void delete(int id) {
        bookingStorage.delete(id);
        log.info("Booking with id {} removed", id);
    }

    @Override
    public Collection<Booking> getAll() {
        Collection<Booking> bookings = bookingStorage.getAll();
        log.info("All bookings received: {}", bookings);
        return bookings;
    }
}
