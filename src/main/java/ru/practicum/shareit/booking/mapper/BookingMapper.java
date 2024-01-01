package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingRequest;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingRequest dto);

    BookingResponse toResponse(Booking booking);
}
