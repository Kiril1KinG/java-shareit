package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingResponse add(@RequestBody BookingRequest request) {
        Booking booking = bookingService.add(mapper.toBooking(request));
        return mapper.toResponse(booking);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Integer id) {
        return mapper.toResponse(bookingService.get(id));
    }

    @GetMapping()
    public Collection<Booking> getAll() {
        return bookingService.getAll();
    }

    @PatchMapping("/{id}")
    public BookingResponse update(@PathVariable Integer id, @RequestBody BookingRequest request) {
        Booking booking = bookingService.update(id, mapper.toBooking(request));
        return mapper.toResponse(booking);
    }

    @DeleteMapping("/id")
    public void delete(@PathVariable Integer id) {
        bookingService.delete(id);
    }
}
