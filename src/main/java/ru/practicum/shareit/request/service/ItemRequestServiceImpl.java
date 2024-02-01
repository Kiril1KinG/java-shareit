package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;


@RequiredArgsConstructor
@Component
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;

    public ItemRequest add(ItemRequest itemRequest) {
        ItemRequest res = itemRequestStorage.add(itemRequest);
        log.info("Item request added:{}", res);
        return res;
    }

    public ItemRequest get(int id) {
        if (!itemRequestStorage.contains(id)) {
            throw new DataDoesNotExistsException(
                    String.format("Get item request failed, item request with id %d not exists", id));
        }
        ItemRequest res = itemRequestStorage.get(id);
        log.info("Item request received:{}", res);
        return res;
    }

    public ItemRequest update(ItemRequest itemRequest) {
        itemRequestStorage.update(itemRequest);
        log.info("Item request updated:{}", itemRequest);
        return itemRequest;
    }

    public void delete(int id) {
        itemRequestStorage.delete(id);
        log.info("Item request with id {} removed", id);
    }
}
