package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;
import java.util.Map;

public class ItemRequestService {
    private final Map<Integer, ItemRequest> itemRequests = new HashMap<>();
    private int id = 1;

    public ItemRequest add(ItemRequest itemRequest) {
        itemRequest.setId(id);
        itemRequests.put(id++, itemRequest);
        return itemRequest;
    }

    public ItemRequest get(int id) { 
        return itemRequests.get(id);
    }

    public ItemRequest update(ItemRequest itemRequest) {
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequests.get(itemRequest.getId());
    }

    public void delete(int id) {
        itemRequests.remove(id);
    }
}
