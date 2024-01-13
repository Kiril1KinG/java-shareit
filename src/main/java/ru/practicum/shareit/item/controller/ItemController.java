package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping()
    public ItemResponse add(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemCreateRequest request) {
        log.info("POST /items X-Sharer-User-Id: {}", userId);
        return mapper.toResponse(itemService.add(userId, mapper.toItem(request)));
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable int id) {
        log.info("GET /items/{}", id);
        return mapper.toResponse(itemService.get(id));
    }

    @GetMapping()
    public Collection<ItemResponse> getAllForOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("GET /items X-Sharer-User-Id: {}", userId);
        return itemService.getByOwnerId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemResponse> search(@RequestParam String text) {
        log.info("GET /items/search?text={}", text);
        return itemService.search(text);
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int id,
                       @RequestBody ItemUpdateRequest request) {
        log.info("PATCH /items/{} X-Sharer-User-Id: {}", id, userId);
        return itemService.update(userId, id, mapper.toItem(request));
    }


}
