package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestStorage {

    ItemRequest add(ItemRequest itemRequest);

    ItemRequest get(int id);

    ItemRequest update(ItemRequest itemRequest);

    void delete(int id);

    boolean contains(int id);
}
