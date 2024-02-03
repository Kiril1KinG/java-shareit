package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Entity.BookingEntity;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingRequest dto);

    Booking toBooking(BookingEntity bookingEntity);

    BookingResponse toResponse(Booking booking);

    BookingEntity toBookingEntity(Booking booking);
}
