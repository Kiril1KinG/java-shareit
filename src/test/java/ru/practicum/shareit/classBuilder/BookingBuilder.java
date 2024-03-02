package ru.practicum.shareit.classBuilder;

import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingBuilder {

    public static Booking buildBooking(Integer id, LocalDateTime start, LocalDateTime end, Item item, User booker,
                                       BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }

    public static BookingEntity buildBookingEntity(Integer id, LocalDateTime start, LocalDateTime end, ItemEntity item,
                                                   UserEntity booker, BookingStatus status) {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(id);
        bookingEntity.setStart(start);
        bookingEntity.setEnd(end);
        bookingEntity.setItem(item);
        bookingEntity.setBooker(booker);
        bookingEntity.setStatus(status);
        return bookingEntity;
    }
}
