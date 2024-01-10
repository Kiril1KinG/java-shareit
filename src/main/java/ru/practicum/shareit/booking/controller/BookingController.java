package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingResponse add(@RequestBody BookingRequest request) {
        log.info("POST /bookings");
        Booking booking = bookingService.add(mapper.toBooking(request));
        return mapper.toResponse(booking);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Integer id) {
        log.info("GET /bookings/{}", id);
        return mapper.toResponse(bookingService.get(id));
    }

    @GetMapping()
    public Collection<Booking> getAll() {
        log.info("GET /bookings");
        return bookingService.getAll();
    }

    @PatchMapping("/{id}")
    public BookingResponse update(@PathVariable Integer id, @RequestBody BookingRequest request) {
        log.info("PATCH /bookings/{}", id);
        Booking booking = bookingService.update(id, mapper.toBooking(request));
        return mapper.toResponse(booking);
    }

    @DeleteMapping("/id")
    public void delete(@PathVariable Integer id) {
        log.info("DELETE /bookings/{}", id);
        bookingService.delete(id);
    }
}
