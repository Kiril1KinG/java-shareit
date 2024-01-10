package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotExistsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    @Override
    public Item add(int userId, Item item) {
        if (!userStorage.contains(userId)) {
            throw new DataNotExistsException(String.format("Add item failed, user with id %d npt exists", userId));
        }
        item.setUserId(userId);
        return itemStorage.add(item);
    }

    @Override
    public Item get(int id) {
        if (!itemStorage.contains(id)) {
            throw new DataNotExistsException(String.format("Get item by id failed, item with %d not exists", id));
        }
        return itemStorage.get(id);
    }

    @Override
    public Item update(int userId, int id, Item item) {
        if (!itemStorage.contains(id)) {
            throw new DataNotExistsException(String.format("Update item failed, item with %d not exists", id));
        }
        if (!userStorage.contains(userId)) {
            throw new DataNotExistsException(String.format("Update item failed, user with %d not exists", userId));
        }
        if (itemStorage.get(id).getUserId() != userId) {
            throw new DataNotExistsException(String.format("Update item failed, user with %d not owner", userId));
        }
        return itemStorage.update(id, item);
    }

    @Override
    public void delete(int userId, int id) {
        if ((itemStorage.get(id).getUserId() != userId)) {
            throw new DataNotExistsException(String.format("Delete item failed, user with %d not owner", userId));
        }
       itemStorage.delete(id);
    }


    @Override
    public Collection<Item> search(String text) {
        return itemStorage.getAll().stream()
                .filter(item -> (item.getName().contains(text) || item.getDescription().contains(text))
                        && item.getAvailable() == true)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllForOwner(int userId) {
        return itemStorage.getAll().stream()
                .filter(item -> item.getUserId() == userId)
                .collect(Collectors.toList());
    }
}
