package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody BookingRequest request,
                                      @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("POST /bookings");
        return client.add(request, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrReject(@PathVariable Integer bookingId,
                                                  @RequestHeader(X_SHARER_USER_ID) Long userId,
                                                  @RequestParam boolean approved) {
        log.info("POST /bookings/{}?approved={}, X-Sharer-User-Id={}", bookingId, approved, userId);
        return client.approveOrReject(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable Integer bookingId,
                                          @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /bookings/{}, X-Sharer-User-Id={}", bookingId, userId);

        return client.getById(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByState(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestParam(value = "state", required = false) String state,
                                                @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        return client.getAllByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForItemsByState(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                                @RequestParam(value = "state", required = false) String state,
                                                                @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                                @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        return client.getAllBookingsForItemsByState(userId, state, from, size);
    }
}