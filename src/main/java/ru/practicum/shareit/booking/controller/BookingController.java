package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;
    private final BookingMapper mapper;

    @PostMapping
    public BookingResponse add(@RequestBody BookingRequest request) {
        Booking booking = service.add(mapper.toBooking(request));
        return mapper.toResponse(booking);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Integer id) {
        return mapper.toResponse(service.get(id));
    }

    @PatchMapping //TODO PATCH /items/{itemId}
    public BookingResponse update(@RequestBody BookingRequest request) {
        Booking booking = service.update(mapper.toBooking(request));
        return mapper.toResponse(booking);
    }

    //TODO /items/search?text={text}

    @DeleteMapping
    public void delete(@RequestHeader("X-Sharer-User-Id") Integer id) {
        service.delete(id);
    }
}
