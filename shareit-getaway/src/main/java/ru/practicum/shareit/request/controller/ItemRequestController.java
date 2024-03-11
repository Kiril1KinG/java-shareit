package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient client;

    @PostMapping()
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestRequest request,
                                         @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("POST /requests, X-Sharer-User-Id: {}", userId);
        return client.create(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /requests, X-Sharer-User-Id: {}", userId);
        return client.getAllForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestParam(value = "from", required = false) @Min(0) Integer from,
                                         @RequestParam(value = "size", required = false) @Min(1) Integer size,
                                         @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("GET /requests/all");
        return client.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Integer requestId,
                                          @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /requests/{}", requestId);
        return client.getById(requestId, userId);
    }

}
