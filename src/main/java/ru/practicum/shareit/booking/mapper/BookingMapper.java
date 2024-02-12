package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Entity.BookingEntity;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Mapping(target = "booker.id", source = "userId")
    @Mapping(target = "item.id", source = "request.itemId")
    public abstract Booking toBooking(BookingRequest request, Integer userId);

    @Mapping(target = "item.request.id", source = "item.request")
    public abstract Booking toBooking(BookingEntity bookingEntity);

    public abstract BookingResponse toResponse(Booking booking);

    @Mapping(target = "item.id", source = "booking.item.id")
    @Mapping(target = "booker.id", source = "booking.booker.id")
    @Mapping(target = "item.request", source = "item.request.id")
    public abstract BookingEntity toBookingEntity(Booking booking);

    public abstract BookingShortDto toBookingResponseShort(BookingEntity booking);
}