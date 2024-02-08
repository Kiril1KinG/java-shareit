package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;

    @Override
    public Item add(int userId, Item item) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Add item failed, user with id %d npt exists", userId));
        }
        item.setOwner(userMapper.toUser(user.get()));
        ItemEntity res = itemMapper.toItemEntity(item);
        itemRepository.save(res);
        log.info("Item added: {}", res);
        return itemMapper.toItem(res);
    }

    @Override
    public Item get(int id, Integer userId) {
        Optional<ItemEntity> itemEntity = itemRepository.findById(id);
        if (itemEntity.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Get item by id failed, item with %d not exists", id));
        }
        Item item = itemMapper.toItem(itemEntity.get());
        if (item.getOwner().getId().equals(userId)) {
            addBookingsToItems(Collections.singleton(item));
        }
        log.info("Item received: {}", item);
        return item;
    }

    @Override
    public Item update(int userId, Item item) {
        Optional<ItemEntity> itemEntity = itemRepository.findById(item.getId());
        if (itemEntity.isEmpty()) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, item with %d not exists", item.getId()));
        }
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, user with %d not exists", userId));
        }
        if (itemEntity.get().getOwner().getId() != userId) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, user with %d not owner", userId));
        }

        if (item.getName() != null) {
            itemEntity.get().setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemEntity.get().setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemEntity.get().setAvailable(item.getAvailable());
        }
        itemRepository.save(itemEntity.get());
        log.info("Item updated: {}", itemEntity);
        return itemMapper.toItem(itemEntity.get());
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
        Collection<Item> items = itemRepository.findUsersByOwnerIdOrderByIdAsc(userId).stream()
                .map(itemMapper::toItem)
                .collect(Collectors.toList());
        addBookingsToItems(items);
        log.info("Items for owner received: {}", items);
        return items;
    }

    private void addBookingsToItems(Collection<Item> items) {
        for (Item item : items) {
            item.setLastBooking(bookingMapper.toBooking(bookingRepository.findLastBookingByItemId(item.getId(), LocalDateTime.now())));
            item.setNextBooking(bookingMapper.toBooking(bookingRepository.findNextBookingByItemId(item.getId(), LocalDateTime.now())));
        }
    }
}
