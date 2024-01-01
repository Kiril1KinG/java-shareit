package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;


@Component
public class ItemService {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;

    public Item add(Item item) {
        item.setId(id);
        items.put(id++, item);
        return item;
    }

    public Item get(int id) {
        return items.get(id);
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public void delete(int id) {
        items.remove(id);
    }
}
