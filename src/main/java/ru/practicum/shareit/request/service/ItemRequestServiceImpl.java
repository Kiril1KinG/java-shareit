package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        UserEntity userEntity = userRepository.findById(itemRequest.getRequestor().getId()).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Create itemRequest failed, user with id %d not exists",
                                itemRequest.getRequestor().getId())));

        itemRequest.setRequestor(userMapper.toUser(userEntity));
        return itemRequestMapper.toItemRequest(itemRequestRepository.save(itemRequestMapper.toEntity(itemRequest)));
    }

    @Override
    public Collection<ItemRequest> getAllForUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Get itemRequests failed, user with id %d not exists", userId));
        }
        Collection<ItemRequest> itemRequests = List.copyOf(itemRequestMapper.toItemRequests(itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId)));
        itemRequests.forEach(i -> i.setItems(List.copyOf(itemMapper.toItems(itemRepository.findAllByRequestRequestorId(userId)))));
        return itemRequests;
    }

    @Override
    public Collection<ItemRequest> getAll(Integer userId, Integer from, Integer size) {
        if (from == null ^ size == null) {
            throw new PaginationParamsException("Bad pagination params, one of the parameters cannot be null");
        }
        Pageable pageable;
        if (from != null & size != null) {
            pageable = PageRequest.of(from, size, Sort.by("created").descending());
        } else {
            int count = (int) itemRequestRepository.count();
            pageable = PageRequest.of(0, count > 0 ? count : 1, Sort.by("created").descending());
        }
        Collection<ItemRequest> itemRequests = itemRequestMapper.toItemRequests(
                itemRequestRepository.findAllWithoutRequestor(userId, pageable).getContent());
        itemRequests.forEach(i -> i.setItems(List.copyOf(itemMapper.toItems(itemRepository.findAllByRequestId(i.getId())))));
        return itemRequests;
    }

    @Override
    public ItemRequest getById(Integer userId, Integer id) {
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Get ItemRequest by id failed, user with id %d not exists", userId));
        }
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestRepository.findById(id)
                .orElseThrow(() -> new DataDoesNotExistsException(
                        String.format("Get ItemRequest by id failed, itemRequest with id %d not exists", id))));
        itemRequest.setItems(List.copyOf(itemMapper.toItems(itemRepository.findAllByRequestId(itemRequest.getId()))));
        return itemRequest;
    }
}