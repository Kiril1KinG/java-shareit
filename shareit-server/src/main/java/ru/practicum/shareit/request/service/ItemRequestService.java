package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequest create(ItemRequest itemRequest);

    Collection<ItemRequest> getAllForUser(Integer userId);

    Collection<ItemRequest> getAll(Integer userId, Integer from, Integer size);

    ItemRequest getById(Integer userId, Integer id);
}