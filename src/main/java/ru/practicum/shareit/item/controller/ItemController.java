package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
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
public class ItemController {

    private final ItemService itemService;

    private final ItemMapper mapper;

    @PostMapping()
    public Item add(@RequestHeader("X-Sharer-User-Id") Integer userId, ItemRequest request) {
        return itemService.add(userId, mapper.toItem(request));
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable int id) {
        return itemService.get(id);
    }

    @GetMapping()
    public Collection<Item> getAllForOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllForOwner(userId);
    }

    @GetMapping("/search?text={text}")
    public Collection<Item> search(@PathVariable String text) {
        return itemService.search(text);
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int id, ItemRequest request) {
        return itemService.update(userId, id, mapper.toItem(request));
    }


}
