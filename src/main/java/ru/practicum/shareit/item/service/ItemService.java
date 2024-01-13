package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item add(int userId, Item item);

    Item get(int id);

    Item update(int userId, int id, Item item);

    void delete(int userId, int id);

    Collection<ItemResponse> search(String text);

    Collection<ItemResponse> getByOwnerId(int userId);
}
