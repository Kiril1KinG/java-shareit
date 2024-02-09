package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class ItemInMemoryStorageImpl implements ItemRequestStorage {
    private final Map<Integer, ItemRequest> itemRequests = new HashMap<>();
    private int id = 1;

    @Override
    public ItemRequest add(ItemRequest itemRequest) {
        itemRequest.setId(id);
        itemRequests.put(id++, itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest get(int id) {
        return itemRequests.get(id);
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public void delete(int id) {
        itemRequests.remove(id);
    }

    @Override
    public boolean contains(int id) {
        return itemRequests.containsKey(id);
    }
}