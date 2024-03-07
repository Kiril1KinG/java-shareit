package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.classBuilder.TestItemRequestProvider;
import ru.practicum.shareit.classBuilder.TestUserProvider;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private UserMapper userMapper;
    private ItemRequestMapper itemRequestMapper;
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
        itemMapper = Mappers.getMapper(ItemMapper.class);
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository,
                userMapper, itemRequestMapper, itemMapper);
    }

    @Test
    void create() {
        ItemRequest itemRequestWithIncorrectRequestorId = TestItemRequestProvider.provideItemRequest(1, "desc",
                TestUserProvider.buildUser(99, "user", "email"),
                LocalDateTime.now(), null);

        ItemRequest itemRequest = TestItemRequestProvider.provideItemRequest(1, "desc",
                TestUserProvider.buildUser(1, "user", "email"),
                LocalDateTime.now(), null);


        when(userRepository.findById(99)).thenReturn(Optional.empty());

        when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(itemRequest.getRequestor())));
        when(itemRequestRepository.save(any())).thenReturn(itemRequestMapper.toEntity(itemRequest));


        assertThrows(DataDoesNotExistsException.class, () -> itemRequestService.create(itemRequestWithIncorrectRequestorId));
        verify(itemRequestRepository, never()).save(any());

        assertEquals(itemRequest, itemRequestService.create(itemRequest));
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void getAllForUser() {
        ItemRequest itemRequest = TestItemRequestProvider.provideItemRequest(1, "desc",
                TestUserProvider.buildUser(1, "user", "email"),
                LocalDateTime.now(), null);

        ItemRequestEntity itemRequestEntity = itemRequestMapper.toEntity(itemRequest);


        when(userRepository.existsById(99)).thenReturn(false);

        when(userRepository.existsById(1)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1)).thenReturn(Collections.singletonList(itemRequestEntity));
        when(itemRepository.findAllByRequestRequestorId(1)).thenReturn(Collections.singletonList(new ItemEntity()));


        assertThrows(DataDoesNotExistsException.class, () -> itemRequestService.getAllForUser(99));
        verify(itemRequestRepository, never()).findAllByRequestorIdOrderByCreatedDesc(any());
        verify(itemRepository, never()).findAllByRequestRequestorId(any());

        assertEquals(Collections.singletonList(itemRequest), itemRequestService.getAllForUser(1));
        verify(itemRequestRepository, times(1)).findAllByRequestorIdOrderByCreatedDesc(any());
        verify(itemRepository, times(1)).findAllByRequestRequestorId(any());
    }

    @Test
    void getAll() {
        ItemRequest itemRequest = TestItemRequestProvider.provideItemRequest(1, "desc",
                TestUserProvider.buildUser(1, "user", "email"),
                LocalDateTime.now(), null);
        ItemRequestEntity itemRequestEntity = itemRequestMapper.toEntity(itemRequest);

        Page<ItemRequestEntity> page = new PageImpl(List.of(itemRequestEntity));


        when(itemRequestRepository.findAllWithoutRequestor(any(), any())).thenReturn(page);
        when(itemRepository.findAllByRequestId(any())).thenReturn(List.of(new ItemEntity()));

        when(itemRequestRepository.findAllWithoutRequestor(eq(1), any())).thenReturn(page);
        when(itemRepository.findAllByRequestId(any())).thenReturn(List.of(new ItemEntity()));


        assertThrows(PaginationParamsException.class, () -> itemRequestService.getAll(1, 0, null));
        verify(itemRequestRepository, never()).findAllWithoutRequestor(eq(1), any());
        verify(itemRepository, never()).findAllByRequestId(eq(1));

        assertEquals(List.of(itemRequest), itemRequestService.getAll(1, 0, 1));
        verify(itemRequestRepository, times(1)).findAllWithoutRequestor(any(), any());
        verify(itemRepository, times(1)).findAllByRequestId(any());
    }

    @Test
    void getById() {
        ItemRequest itemRequest = TestItemRequestProvider.provideItemRequest(1, "desc",
                TestUserProvider.buildUser(1, "user", "email"),
                LocalDateTime.now(), null);


        when(userRepository.existsById(99)).thenReturn(false);

        when(userRepository.existsById(1)).thenReturn(true);
        when(itemRequestRepository.findById(99)).thenReturn(Optional.empty());

        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequestMapper.toEntity(itemRequest)));
        when(itemRepository.findAllByRequestId(1)).thenReturn(Collections.emptyList());


        assertThrows(DataDoesNotExistsException.class, () -> itemRequestService.getById(99, 1));
        verify(itemRepository, never()).findAllByRequestId(eq(1));

        assertThrows(DataDoesNotExistsException.class, () -> itemRequestService.getById(1, 99));
        verify(itemRepository, never()).findAllByRequestId(eq(1));

        assertEquals(itemRequest, itemRequestService.getById(1, 1));
        verify(itemRepository, times(1)).findAllByRequestId(eq(1));
    }
}