package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingStorage {

    Booking add(Booking booking);

    Booking get(int id);

    Booking update(int id, Booking booking);

    void delete(int id);

    boolean contains(int id);

    Collection<Booking> getAll();
}
