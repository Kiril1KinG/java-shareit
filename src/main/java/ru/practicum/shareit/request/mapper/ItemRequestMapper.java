package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    Item toItemRequest(ItemRequestRequest dto);

    ItemRequestResponse toResponse(Item item);
}