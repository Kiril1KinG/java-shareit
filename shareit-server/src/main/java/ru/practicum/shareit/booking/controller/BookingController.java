package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingResponse add(@Valid @RequestBody BookingRequest request,
                               @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("POST /bookings");
        Booking booking = mapper.toBooking(request, userId);
        return mapper.toResponse(bookingService.add(booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approveOrReject(@PathVariable Integer bookingId,
                                           @RequestHeader(X_SHARER_USER_ID) Integer userId,
                                           @RequestParam boolean approved) {
        log.info("POST /bookings/{}?approved={}, X-Sharer-User-Id={}", bookingId, approved, userId);
        return mapper.toResponse(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@PathVariable Integer bookingId,
                                   @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /bookings/{}, X-Sharer-User-Id={}", bookingId, userId);

        return mapper.toResponse(bookingService.getById(bookingId, userId));
    }

    @GetMapping()
    public Collection<BookingResponse> getAllByState(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                                     @RequestParam(value = "state", required = false) String state,
                                                     @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                     @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        return bookingService.getAllBookingsByState(userId, state, from, size).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingResponse> getAllBookingsForItemsByState(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                                                     @RequestParam(value = "state", required = false) String state,
                                                                     @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                                     @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        return bookingService.getAllBookingsForItemsByState(userId, state, from, size).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}