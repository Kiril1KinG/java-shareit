package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping()
    public Item add(@RequestHeader("X-Sharer-User-Id") Integer userId, ItemRequest request) {
        log.info("POST /items \n X-Sharer-User-Id: {}", userId);
        return itemService.add(userId, mapper.toItem(request));
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable int id) {
        log.info("GET /items/{}", id);
        return itemService.get(id);
    }

    @GetMapping()
    public Collection<Item> getAllForOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("GET /items \n X-Sharer-User-Id: {}", userId);
        return itemService.getAllForOwner(userId);
    }

    @GetMapping("/search?text={text}")
    public Collection<Item> search(@PathVariable String text) {
        log.info("GET /items/search?text={}", text);
        return itemService.search(text);
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int id, ItemRequest request) {
        log.info("PATCH /items/{} \n X-Sharer-User-Id: {}", id, userId);
        return itemService.update(userId, id, mapper.toItem(request));
    }


}
