package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestResponseWithItems;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestRequest dto);

    ItemRequest toItemRequest(ItemRequestEntity itemRequestEntity);

    ItemRequestResponse toResponse(ItemRequest itemRequest);

    ItemRequestEntity toEntity(ItemRequest itemRequest);

    Collection<ItemRequestResponse> toResponses(Collection<ItemRequest> itemRequests);

    ItemRequestResponseWithItems toResponseWithItems(ItemRequest itemRequest);

    Collection<ItemRequest> toItemRequests(Collection<ItemRequestEntity> itemRequestEntities);
}