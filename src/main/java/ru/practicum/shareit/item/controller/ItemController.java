package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping()
    public ItemResponse add(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                            @RequestBody @Valid ItemCreateRequest request) {
        log.info("POST /items X-Sharer-User-Id: {}", userId);
        return itemMapper.toResponse(itemService.add(userId, itemMapper.toItem(request)));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsResponse get(@PathVariable Integer itemId,
                                        @RequestHeader(value = X_SHARER_USER_ID) Integer userId) {
        log.info("GET /items/{}", itemId);
        return itemMapper.toItemWithBookingsResponse(itemService.get(itemId, userId));
    }

    @GetMapping()
    public Collection<ItemWithBookingsResponse> getAllForOwner(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                                               @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                               @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        log.info("GET /items X-Sharer-User-Id: {}", userId);
        return itemService.getByOwnerId(userId, from, size).stream()
                .map(itemMapper::toItemWithBookingsResponse)
                .collect(Collectors.toList());

    }

    @GetMapping("/search")
    public Collection<ItemResponse> search(@RequestParam String text,
                                           @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                           @RequestParam(value = "size", required = false) @Min(1) Integer size) {
        log.info("GET /items/search?text={}", text);
        return itemService.search(text, from, size).stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable Integer id,
                       @Valid @RequestBody ItemUpdateRequest request) {
        log.info("PATCH /items/{} X-Sharer-User-Id: {}", id, userId);
        Item item = itemMapper.toItem(request);
        item.setId(id);
        return itemService.update(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(@PathVariable Integer itemId, @RequestHeader(X_SHARER_USER_ID) Integer userId,
                                      @Valid @RequestBody CommentRequest request) {
        log.info("POST items/{}/comment, X-Sharer-User-Id: {}", itemId, userId);
        return commentMapper.toResponse(itemService.addComment(commentMapper.toComment(request, itemId, userId)));
    }
}