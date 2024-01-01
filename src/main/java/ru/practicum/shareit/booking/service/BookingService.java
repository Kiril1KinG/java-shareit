package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.util.HashMap;
import java.util.Map;


@Component
public class BookingService {
    private final Map<Integer, Booking> bookings = new HashMap<>();
    private int id = 1;

    public Booking add(Booking booking) {
        booking.setId(id);
        bookings.put(id++, booking);
        return booking;
    }

    public Booking get(int id) {
        return bookings.get(id);
    }

    public Booking update(Booking booking) {
        bookings.put(booking.getId(), booking);
        return bookings.get(booking.getId());
    }

    public void delete(int id) {
        bookings.remove(id);
    }

}
