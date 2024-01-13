package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorageImpl implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;

    @Override
    public Item add(Item item) {
        item.setId(id);
        items.put(id++, item);
        return item;
    }

    @Override
    public Item get(int id) {
        return items.get(id);
    }

    @Override
    public Item update(int id, Item item) {
        items.put(id, item);
        return item;
    }

    @Override
    public void delete(int id) {
        items.remove(id);
    }

    @Override
    public Collection<Item> getAll() {
        return items.values();
    }

    @Override
    public boolean contains(int id) {
        return items.containsKey(id);
    }

    @Override
    public Collection<Item> search(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable())
                .collect(Collectors.toList());
    }
}
