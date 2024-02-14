package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
@Validated
public class ItemRequestController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper mapper;

    @PostMapping()
    public ItemRequestResponse create(@Valid @RequestBody ItemRequestRequest request,
                                      @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("POST /requests, X-Sharer-User-Id: {}", userId);
        request.setCreated(LocalDateTime.now());
        request.setRequestor(new User());
        request.getRequestor().setId(userId);
        return mapper.toResponse(itemRequestService.create(mapper.toItemRequest(request)));
    }

    @GetMapping
    public Collection<ItemRequestResponseWithItems> getAllForUser(@RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /requests, X-Sharer-User-Id: {}", userId);
        return itemRequestService.getAllForUser(userId).stream()
                .map(mapper::toResponseWithItems)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseWithItems> getAll(@RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                           @RequestParam(value = "size", required = false) @Min(1) Integer size,
                                                           @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /requests/all");
        return itemRequestService.getAll(userId, from, size).stream()
                .map(mapper::toResponseWithItems)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseWithItems getById(@PathVariable Integer requestId,
                                                @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /requests/{}", requestId);
        return mapper.toResponseWithItems(itemRequestService.getById(userId, requestId));
    }
}