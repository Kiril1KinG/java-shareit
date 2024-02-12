package ru.practicum.shareit.booking.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class BookingInMemoryStorageImpl implements BookingStorage {
    private final Map<Integer, Booking> bookings = new HashMap<>();
    private int id = 1;

    @Override
    public Booking add(Booking booking) {
        booking.setId(id);
        bookings.put(id++, booking);
        return booking;
    }

    @Override
    public Booking get(int id) {
        return bookings.get(id);
    }

    @Override
    public Booking update(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public void delete(int id) {
        bookings.remove(id);
    }

    @Override
    public boolean contains(int id) {
        return bookings.containsKey(id);
    }

    @Override
    public Collection<Booking> getAll() {
        return bookings.values();
    }
}