package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;


public interface ItemRequestService {

    ItemRequest add(ItemRequest itemRequest);

    ItemRequest get(int id);

    ItemRequest update(ItemRequest itemRequest);

    void delete(int id);
}
