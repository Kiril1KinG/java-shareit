package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item add(int userId, Item item);

    Item get(int itemId, Integer userId);

    Item update(int userId, Item item);

    void delete(int userId, int id);

    Collection<Item> search(String text, Integer from, Integer size);

    Collection<Item> getByOwnerId(int userId, Integer from, Integer size);

    Comment addComment(Comment comment);
}