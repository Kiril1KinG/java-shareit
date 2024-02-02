package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public Item add(int userId, Item item) {
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Add item failed, user with id %d npt exists", userId));
        }
        item.setOwnerId(userId);
        ItemEntity res = itemMapper.toItemEntity(item);
        res.setOwner(userRepository.findById(userId).get());
        itemRepository.save(res);
        log.info("Item added: {}", res);
        return itemMapper.toItem(res);
    }

    @Override
    public Item get(int id) {
        if (!itemRepository.existsById(id)) {
            throw new DataDoesNotExistsException(
                    String.format("Get item by id failed, item with %d not exists", id));
        }
        Optional<ItemEntity> res = itemRepository.findById(id);
        log.info("Item received: {}", res.get());
        return itemMapper.toItem(res.get());
    }

    @Override
    public Item update(int userId, Item item) {
        if (!itemRepository.existsById(item.getId())) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, item with %d not exists", item.getId()));
        }
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, user with %d not exists", userId));
        }
        if (itemRepository.findById(item.getId()).get().getOwner().getId() != userId) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, user with %d not owner", userId));
        }
        Optional<ItemEntity> modified = itemRepository.findById(item.getId());
        if (item.getName() != null) {
            modified.get().setName(item.getName());
        }
        if (item.getDescription() != null) {
            modified.get().setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            modified.get().setAvailable(item.getAvailable());
        }
        itemRepository.save(modified.get());
        log.info("Item updated: {}", modified);
        return itemMapper.toItem(modified.get());
    }

    @Override
    public void delete(int userId, int id) {
        if ((itemRepository.findById(id).get().getOwner().getId() != userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Delete item failed, user with %d not owner", userId));
        }
        itemRepository.deleteById(id);
        log.info("Item with id {} deleted", id);
    }


    @Override
    public Collection<Item> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<ItemEntity> items = itemRepository.search(text);
        log.info("Item search by request \"{}\" received: {}", text, items);
        return itemMapper.toItems(items);
    }

    @Override
    public Collection<Item> getByOwnerId(int userId) {
        Collection<ItemEntity> items = itemRepository.findUsersByOwnerId(userId);
        log.info("Items for owner received: {}", items);
        return itemMapper.toItems(items);
    }
}
