package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponse;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemCreateRequest request);

    Item toItem(ItemUpdateRequest request);

    @Mapping(target = "request.id", source = "request")
    Item toItem(ItemEntity itemEntity);

    ItemResponse toResponse(Item item);

    @Mapping(target = "request", source = "request.id")
    ItemEntity toItemEntity(Item item);

    Collection<Item> toItems(Collection<ItemEntity> itemEntities);

    @Mapping(target = "lastBooking.bookerId", source = "lastBooking.booker.id")
    @Mapping(target = "nextBooking.bookerId", source = "nextBooking.booker.id")
    ItemWithBookingsResponse toItemWithBookingsResponse(Item items);

}
