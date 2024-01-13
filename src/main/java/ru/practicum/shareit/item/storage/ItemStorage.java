package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item add(Item item);

    Item get(int id);

    Item update(int id, Item item);

    void delete(int id);

    Collection<Item> getAll();

    boolean contains(int id);

    Collection<Item> search(String text);
}
