package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ItemServiceImplTest {

    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private ItemService itemService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemRequestRepository itemRequestRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;
    private CommentMapper commentMapper;

    @BeforeEach
    void setup() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemMapper = Mappers.getMapper(ItemMapper.class);
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        commentMapper = Mappers.getMapper(CommentMapper.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, itemRequestRepository, bookingRepository,
                commentRepository, itemMapper, bookingMapper, commentMapper);
    }

    @Test
    void add() {
        Item itemWithIncorrectRequestId = new Item(1, "item", "desc", true, null,
                new ItemRequest(99, null, null, null, null),
                null, null, null);

        Item item = new Item(2, "item", "desc", true, null,
                new ItemRequest(1, null, null, null, null),
                null, null, null);

        Item item2 = new Item(3, "item", "desc", true,
                new User(1, "user", "email"),
                new ItemRequest(2, null, null, null, null),
                null, null, null);

        ItemEntity item2Entity = itemMapper.toItemEntity(item2);
        item2Entity.setRequest(itemRequestMapper.toEntity(item2.getRequest()));
        item2Entity.setOwner(userMapper.toUserEntity(item2.getOwner()));

        Mockito.when(itemRequestRepository.findById(99)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(99)).thenReturn(Optional.empty());
        Mockito.when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequestMapper.toEntity(item.getRequest())));
        Mockito.when(itemRequestRepository.findById(2)).thenReturn(Optional.of(itemRequestMapper.toEntity(item2.getRequest())));
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(item2.getOwner())));

        Assertions.assertThrows(DataDoesNotExistsException.class, () -> itemService.add(1, itemWithIncorrectRequestId));
        Mockito.verify(itemRepository, Mockito.never()).save(itemMapper.toItemEntity(itemWithIncorrectRequestId));
        Assertions.assertThrows(DataDoesNotExistsException.class, () -> itemService.add(99, item));
        Mockito.verify(itemRepository, Mockito.never()).save(itemMapper.toItemEntity(item));
        Assertions.assertEquals(item2, itemService.add(1, item2));
        Mockito.verify(itemRepository, Mockito.times(1)).save(item2Entity);
    }

    @Test
    void get() {
        Item item = new Item(1, "item", "desc", true,
                new User(1, "user", "email"),
                new ItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);

        Mockito.when(itemRepository.findById(99)).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));
        Mockito.when(commentRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findLastAndNextBookingByItemId(item.getId(), LocalDateTime.now()))
                .thenReturn(List.of(new BookingEntity(), new BookingEntity()));

        Assertions.assertThrows(DataDoesNotExistsException.class, () -> itemService.get(99, 1));
        Assertions.assertEquals(item, itemService.get(1, 1));
    }

    @Test
    void update() {
        Item empty = new Item();
        ItemEntity emptyEntity = itemMapper.toItemEntity(empty);

        Item item = new Item(1, "item", "desc", true,
                new User(1, "user", "email"),
                new ItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);

        Item itemForUpdate = new Item(2, "item", "desc", true,
                new User(2, "user", "email"),
                new ItemRequest(2, null, null, null, null),
                null, null, null);
        ItemEntity itemForUpdateEntity = itemMapper.toItemEntity(itemForUpdate);

        Item update = new Item(2, "item", "new desc", false,
                new User(2, "user", "email"),
                new ItemRequest(2, null, null, null, null),
                null, null, null);
        ItemEntity updateEntity = itemMapper.toItemEntity(update);

        Mockito.when(itemRepository.findById(99)).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(itemEntity));
        Mockito.when(userRepository.existsById(99)).thenReturn(false);
        Mockito.when(userRepository.existsById(3)).thenReturn(true);
        Mockito.when(itemRepository.findById(2)).thenReturn(Optional.of(itemForUpdateEntity));
        Mockito.when(userRepository.existsById(2)).thenReturn(true);

        Assertions.assertThrows(DataDoesNotExistsException.class, () -> itemService.update(1, empty));
        Mockito.verify(itemRepository, Mockito.never()).save(emptyEntity);
        Assertions.assertThrows(DataDoesNotExistsException.class, () -> itemService.update(99, item));
        Assertions.assertThrows(DataDoesNotExistsException.class, () -> itemService.update(3, item));
        Mockito.verify(itemRepository, Mockito.never()).save(itemEntity);
        Assertions.assertEquals(update, itemService.update(2, update));
        Mockito.verify(itemRepository, Mockito.times(1)).save(updateEntity);
    }

    @Test
    void delete() {
    }

    @Test
    void search() {
    }

    @Test
    void getByOwnerId() {
    }

    @Test
    void addComment() {
    }
}