package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotExistsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item add(int userId, Item item) {
        if (!userStorage.contains(userId)) {
            throw new DataNotExistsException(
                    String.format("Add item failed, user with id %d npt exists", userId));
        }
        item.setOwnerId(userId);
        Item res = itemStorage.add(item);
        log.info("Item added: {}", res);
        return res;
    }

    @Override
    public Item get(int id) {
        if (!itemStorage.contains(id)) {
            throw new DataNotExistsException(
                    String.format("Get item by id failed, item with %d not exists", id));
        }
        Item res = itemStorage.get(id);
        log.info("Item received: {}", res);
        return res;
    }

    @Override
    public Item update(int userId, Item item) {
        if (!itemStorage.contains(item.getId())) {
            throw new DataNotExistsException(
                    String.format("Update item failed, item with %d not exists", item.getId()));
        }
        if (!userStorage.contains(userId)) {
            throw new DataNotExistsException(
                    String.format("Update item failed, user with %d not exists", userId));
        }
        if (itemStorage.get(item.getId()).getOwnerId() != userId) {
            throw new DataNotExistsException(
                    String.format("Update item failed, user with %d not owner", userId));
        }
        Item modified = itemStorage.get(item.getId());
        if (item.getName() != null) {
            modified.setName(item.getName());
        }
        if (item.getDescription() != null) {
            modified.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            modified.setAvailable(item.getAvailable());
        }
        itemStorage.update(modified);
        log.info("Item updated: {}", modified);
        return modified;
    }

    @Override
    public void delete(int userId, int id) {
        if ((itemStorage.get(id).getOwnerId() != userId)) {
            throw new DataNotExistsException(
                    String.format("Delete item failed, user with %d not owner", userId));
        }
        itemStorage.delete(id);
        log.info("Item with id {} deleted", id);
    }


    @Override
    public Collection<Item> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemStorage.search(text);
        log.info("Item search by request \"{}\" received: {}", text, items);
        return items;
    }

    @Override
    public Collection<Item> getByOwnerId(int userId) {
        Collection<Item> items = itemStorage.getAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
        log.info("Items for owner received: {}", items);
        return items;
    }
}
