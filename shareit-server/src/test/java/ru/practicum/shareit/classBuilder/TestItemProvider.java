package ru.practicum.shareit.classBuilder;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class TestItemProvider {

    public static Item provideItem(Integer id, String name, String description, Boolean available, User owner,
                                   ItemRequest request, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        item.setComments(comments);
        return item;
    }

    public static ItemEntity provideItemEntity(Integer id, String name, String description, Boolean available, UserEntity owner,
                                               ItemRequestEntity request) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName(name);
        itemEntity.setDescription(description);
        itemEntity.setAvailable(available);
        itemEntity.setOwner(owner);
        itemEntity.setRequest(request);
        return itemEntity;
    }
}
