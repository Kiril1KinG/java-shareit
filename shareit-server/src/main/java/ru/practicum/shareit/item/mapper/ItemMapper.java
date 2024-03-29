package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemShortResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponse;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = CommentMapper.class)
@RequiredArgsConstructor
public abstract class ItemMapper {

    @Mapping(target = "request.id", source = "requestId")
    public abstract Item toItem(ItemCreateRequest request);

    public abstract Item toItem(ItemUpdateRequest request);

    public abstract Item toItem(ItemEntity itemEntity);

    @Mapping(target = "requestId", source = "request.id")
    public abstract ItemResponse toResponse(Item item);

    @Mapping(target = "requestId", source = "request.id")
    public abstract ItemShortResponse toShortResponse(Item item);

    public abstract ItemEntity toItemEntity(Item item);

    public abstract Collection<Item> toItems(Collection<ItemEntity> itemEntities);

    @Mapping(target = "lastBooking.bookerId", source = "lastBooking.booker.id")
    @Mapping(target = "nextBooking.bookerId", source = "nextBooking.booker.id")
    public abstract ItemWithBookingsResponse toItemWithBookingsResponse(Item items);

}