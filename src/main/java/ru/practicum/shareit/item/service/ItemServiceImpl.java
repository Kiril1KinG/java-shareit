package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotExistsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService{

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item add(int userId, Item item) {
        if (!userStorage.contains(userId)) {
            throw new DataNotExistsException(String.format("Add item failed, user with id %d npt exists", userId));
        }
        item.setUserId(userId);
        Item res = itemStorage.add(item);
        log.info("Item added: {}", res);
        return res;
    }

    @Override
    public Item get(int id) {
        if (!itemStorage.contains(id)) {
            throw new DataNotExistsException(String.format("Get item by id failed, item with %d not exists", id));
        }
        Item res = itemStorage.get(id);
        log.info("Item received: {}", res);
        return res;
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
        Item modified = itemStorage.get(id);
        modified.setName(item.getName());
        modified.setDescription(item.getDescription());
        modified.setAvailable(item.getAvailable());
        itemStorage.update(id, modified);
        log.info("Item updated: {}", modified);
        return modified;
    }

    @Override
    public void delete(int userId, int id) {
        if ((itemStorage.get(id).getUserId() != userId)) {
            throw new DataNotExistsException(String.format("Delete item failed, user with %d not owner", userId));
        }
        itemStorage.delete(id);
        log.info("Item with id {} deleted", id);
    }


    @Override
    public Collection<Item> search(String text) {
        Collection<Item> items = itemStorage.getAll().stream()
                .filter(item -> (item.getName().contains(text) || item.getDescription().contains(text))
                        && item.getAvailable() == true)
                .collect(Collectors.toList());
        log.info("Item search by request \"{}\" received: {}", text, items);
        return items;
    }

    @Override
    public Collection<Item> getAllForOwner(int userId) {
        Collection<Item> items = itemStorage.getAll().stream()
                .filter(item -> item.getUserId() == userId)
                .collect(Collectors.toList());
        log.info("Items for owner received: {}", items);
        return items;
    }
}
