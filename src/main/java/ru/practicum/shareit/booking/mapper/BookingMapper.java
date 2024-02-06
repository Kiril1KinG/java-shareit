package ru.practicum.shareit.booking.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.Entity.BookingEntity;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.storage.UserRepository;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "booker.id", source = "userId")
    @Mapping(target = "item.id", source = "request.itemId")
    public abstract Booking toBooking(BookingRequest request, Integer userId);

    public abstract Booking toBooking(BookingEntity bookingEntity);

    public abstract BookingResponse toResponse(Booking booking);

    @Mapping(target = "item.id", source = "booking.item.id")
    @Mapping(target = "booker.id", source = "booking.booker.id")
    public abstract BookingEntity toBookingEntity(Booking booking);
}
