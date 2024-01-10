package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        Item modified = items.get(id);
        modified.setName(item.getName());
        modified.setDescription(item.getDescription());
        modified.setAvailable(item.getAvailable());
        return modified;
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
}
