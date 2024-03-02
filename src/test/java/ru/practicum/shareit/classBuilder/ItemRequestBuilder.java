package ru.practicum.shareit.classBuilder;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestBuilder {

    public static ItemRequest buildItemRequest(Integer id, String description, User requestor, LocalDateTime created,
                                               List<Item> items) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        itemRequest.setItems(items);
        return itemRequest;
    }

    public static ItemRequestEntity buildItemRequestEntity(Integer id, String description, UserEntity requestor, LocalDateTime created) {
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity();
        itemRequestEntity.setId(id);
        itemRequestEntity.setDescription(description);
        itemRequestEntity.setRequestor(requestor);
        itemRequestEntity.setCreated(created);
        return itemRequestEntity;
    }
}
