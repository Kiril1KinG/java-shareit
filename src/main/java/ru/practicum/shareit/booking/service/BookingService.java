package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Booking add(Booking booking);

    Booking approveBooking(Integer bookingId, Integer userId, boolean approve);

    Booking getById(Integer bookingId, Integer userId);

    Collection<Booking> getAllBookingsByState(Integer userId, String bookingState);

    Collection<Booking> getAllBookingsForItemsByState(Integer userId, String bookingState);
}
