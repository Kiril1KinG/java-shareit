package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemCreateRequest request);

    Item toItem(ItemUpdateRequest request);

    Item toItem(ItemEntity itemEntity);

    ItemResponse toResponse(Item item);

    ItemEntity toItemEntity(Item item);

    Collection<Item> toItems(Collection<ItemEntity> itemEntities);

}
