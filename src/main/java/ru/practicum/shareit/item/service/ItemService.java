package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item add(int userId, Item item);

    Item get(int id);

    Item update(int userId, Item item);

    void delete(int userId, int id);

    Collection<Item> search(String text);

    Collection<Item> getByOwnerId(int userId);
}
