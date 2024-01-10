package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotExistsException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;


@RequiredArgsConstructor
@Component
public class ItemRequestServiceImpl implements ItemRequestService{

    private final ItemRequestStorage itemRequestStorage;

    public ItemRequest add(ItemRequest itemRequest) {
        return itemRequestStorage.add(itemRequest);
    }

    public ItemRequest get(int id) {
        if (!itemRequestStorage.contains(id)){
            throw new DataNotExistsException(String.format("Get item request failed, item request with id %d not exists",
                    id));
        }
        return itemRequestStorage.get(id);
    }

    public ItemRequest update(int id, ItemRequest itemRequest) {
        itemRequest.setId(id);
        return itemRequestStorage.update(itemRequest);
    }

    public void delete(int id) {
        itemRequestStorage.delete(id);
    }
}
