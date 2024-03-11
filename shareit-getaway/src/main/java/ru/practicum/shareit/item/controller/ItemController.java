package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient client;

    @PostMapping()
    public ResponseEntity<Object> add(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                      @RequestBody @Valid ItemCreateRequest request) {
        log.info("POST /items X-Sharer-User-Id: {}", userId);
        return client.add(userId, request);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Integer itemId,
                                      @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("GET /items/{}", itemId);
        return client.get(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllForOwner(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                 @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        log.info("GET /items X-Sharer-User-Id: {}", userId);
        return client.getAllForOwner(userId, from, size);

    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                         @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        log.info("GET /items/search?text={}", text);
        return client.search(text, from, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable Integer id,
                                         @Valid @RequestBody ItemUpdateRequest request) {
        log.info("PATCH /items/{} X-Sharer-User-Id: {}", id, userId);
        return client.update(userId, id, request);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Integer itemId, @RequestHeader(X_SHARER_USER_ID) Integer userId,
                                             @Valid @RequestBody CommentRequest request) {
        log.info("POST items/{}/comment, X-Sharer-User-Id: {}", itemId, userId);
        return client.addComment(itemId, userId, request);
    }
}