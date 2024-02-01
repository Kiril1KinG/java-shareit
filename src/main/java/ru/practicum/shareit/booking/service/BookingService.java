package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Booking add(Booking booking);

    Booking get(int id);

    Booking update(Booking booking);

    void delete(int id);

    Collection<Booking> getAll();
}
