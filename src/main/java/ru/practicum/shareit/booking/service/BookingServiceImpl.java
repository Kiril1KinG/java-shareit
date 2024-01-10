package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.storage.BookingInMemoryStorageImpl;
import ru.practicum.shareit.exception.DataNotExistsException;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final BookingStorage bookingStorage;

    @Override
    public Booking add(Booking booking) {
        return bookingStorage.add(booking);
    }

    @Override
    public Booking get(int id) {
        if (!bookingStorage.contains(id)) {
            throw new DataNotExistsException(String.format("Get booking by id filed, booking with id %d not exists",
                    id));
        }
        return bookingStorage.get(id);
    }

    @Override
    public Booking update(int id, Booking booking) {
        if (!bookingStorage.contains(id)) {
            throw new DataNotExistsException(String.format("Update booking by id filed, booking with id %d not exists",
                    id));
        }
        Booking modified = bookingStorage.get(id);
        modified.setEnd(booking.getEnd());
        modified.setStatus(booking.getStatus());
        return bookingStorage.update(id, booking);
    }

    @Override
    public void delete(int id) {
        bookingStorage.delete(id);
    }

    @Override
    public Collection<Booking> getAll() {
        return bookingStorage.getAll();
    }
}
