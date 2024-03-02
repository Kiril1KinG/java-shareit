package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.classBuilder.CommentBuilder;
import ru.practicum.shareit.classBuilder.ItemBuilder;
import ru.practicum.shareit.classBuilder.ItemRequestBuilder;
import ru.practicum.shareit.classBuilder.UserBuilder;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.exception.WithoutBookingException;
import ru.practicum.shareit.item.dto.ItemShortResponse;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemMapper = Mappers.getMapper(ItemMapper.class);
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        commentMapper = Mappers.getMapper(CommentMapper.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, itemRequestRepository, bookingRepository,
                commentRepository, itemMapper, bookingMapper, commentMapper);
    }

    @Test
    void add() {
        Item itemWithIncorrectRequestId = ItemBuilder.buildItem(1, "item", "desc", true, null,
                ItemRequestBuilder.buildItemRequest(99, null, null, null, null),
                null, null, null);

        Item item = ItemBuilder.buildItem(2, "item", "desc", true, null,
                ItemRequestBuilder.buildItemRequest(1, null, null, null, null),
                null, null, null);

        Item item2 = ItemBuilder.buildItem(3, "item", "desc", true,
                UserBuilder.buildUser(1, "user", "email"),
                ItemRequestBuilder.buildItemRequest(2, null, null, null, null),
                null, null, null);

        ItemEntity item2Entity = itemMapper.toItemEntity(item2);
        item2Entity.setRequest(itemRequestMapper.toEntity(item2.getRequest()));
        item2Entity.setOwner(userMapper.toUserEntity(item2.getOwner()));


        when(itemRequestRepository.findById(99)).thenReturn(Optional.empty());
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequestMapper.toEntity(item.getRequest())));
        when(itemRequestRepository.findById(2)).thenReturn(Optional.of(itemRequestMapper.toEntity(item2.getRequest())));
        when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(item2.getOwner())));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.add(1, itemWithIncorrectRequestId));
        verify(itemRepository, never()).save(itemMapper.toItemEntity(itemWithIncorrectRequestId));
        assertThrows(DataDoesNotExistsException.class, () -> itemService.add(99, item));
        verify(itemRepository, never()).save(itemMapper.toItemEntity(item));
        assertEquals(item2, itemService.add(1, item2));
        verify(itemRepository, times(1)).save(item2Entity);
    }

    @Test
    void get() {
        Item item = ItemBuilder.buildItem(1, "item", "desc", true,
                UserBuilder.buildUser(1, "user", "email"),
                ItemRequestBuilder.buildItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStart(LocalDateTime.now());


        when(itemRepository.findById(99)).thenReturn(Optional.empty());
        when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastAndNextBookingByItemId(anyInt(), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingEntity));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.get(99, 1));
        assertEquals(item, itemService.get(1, 1));
    }

    @Test
    void update() {
        Item empty = new Item();
        ItemEntity emptyEntity = itemMapper.toItemEntity(empty);

        Item item = ItemBuilder.buildItem(1, "item", "desc", true,
                UserBuilder.buildUser(1, "user", "email"),
                ItemRequestBuilder.buildItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);

        Item itemForUpdate = ItemBuilder.buildItem(2, "item", "desc", true,
                UserBuilder.buildUser(2, "user", "email"),
                ItemRequestBuilder.buildItemRequest(2, null, null, null, null),
                null, null, null);
        ItemEntity itemForUpdateEntity = itemMapper.toItemEntity(itemForUpdate);

        Item update = ItemBuilder.buildItem(2, "item", "new desc", false,
                UserBuilder.buildUser(2, "user", "email"),
                ItemRequestBuilder.buildItemRequest(2, null, null, null, null),
                null, null, null);
        ItemEntity updateEntity = itemMapper.toItemEntity(update);


        when(itemRepository.findById(null)).thenReturn(Optional.empty());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(itemEntity));
        when(userRepository.existsById(99)).thenReturn(false);
        when(userRepository.existsById(3)).thenReturn(true);
        when(itemRepository.findById(2)).thenReturn(Optional.of(itemForUpdateEntity));
        when(userRepository.existsById(2)).thenReturn(true);


        assertThrows(DataDoesNotExistsException.class, () -> itemService.update(1, empty));
        verify(itemRepository, never()).save(emptyEntity);

        assertThrows(DataDoesNotExistsException.class, () -> itemService.update(99, item));

        assertThrows(DataDoesNotExistsException.class, () -> itemService.update(3, item));
        verify(itemRepository, never()).save(itemEntity);

        assertEquals(update, itemService.update(2, update));
        verify(itemRepository, times(1)).save(updateEntity);
    }

    @Test
    void delete() {
        Item itemWithIncorrectOwnerId = ItemBuilder.buildItem(1, "item", "desc", true,
                UserBuilder.buildUser(99, "user", "email"),
                ItemRequestBuilder.buildItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntityWithIncorrectOwnerId = itemMapper.toItemEntity(itemWithIncorrectOwnerId);

        Item item = ItemBuilder.buildItem(2, "item", "desc", true,
                UserBuilder.buildUser(2, "user", "email"),
                ItemRequestBuilder.buildItemRequest(2, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);


        when(itemRepository.findById(99)).thenReturn(Optional.empty());
        when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntityWithIncorrectOwnerId));
        when(itemRepository.findById(2)).thenReturn(Optional.of(itemEntity));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.delete(1, 99));
        verify(itemRepository, never()).deleteById(1);

        assertThrows(DataDoesNotExistsException.class, () -> itemService.delete(1, 1));
        verify(itemRepository, never()).deleteById(1);

        itemService.delete(2, 2);
        verify(itemRepository, times(1)).deleteById(2);
    }

    @Test
    void search() {
        Item item = ItemBuilder.buildItem(1, "item", "desc", true,
                UserBuilder.buildUser(1, "user", "email"),
                ItemRequestBuilder.buildItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);
        Page<ItemEntity> page = new PageImpl<>(List.of(itemEntity));


        when(itemRepository.search(eq("search"), any())).thenReturn(page);


        assertEquals(Collections.emptyList(), itemService.search(" ", 0, 2));
        verify(itemRepository, never()).search(anyString(), any());

        assertThrows(PaginationParamsException.class, () -> itemService.search("text", 0, null));
        verify(itemRepository, never()).search(anyString(), any());

        assertEquals(List.of(item), itemService.search("search", 0, 1));
        verify(itemRepository, times(1)).search(anyString(), any());
    }

    @Test
    void getByOwnerId() {
        Item item = ItemBuilder.buildItem(1, "item", "desc", true,
                UserBuilder.buildUser(1, "user", "email"),
                null, null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);
        Page<ItemEntity> page = new PageImpl<>(List.of(itemEntity));


        when(itemRepository.findAllByOwnerId(anyInt(), any())).thenReturn(page);


        assertThrows(PaginationParamsException.class, () -> itemService.getByOwnerId(99, 0, null));
        verify(itemRepository, never()).findAllByOwnerId(anyInt(), any());

        assertEquals(List.of(item), itemService.getByOwnerId(1, 0, 1));
        verify(itemRepository, times(1)).findAllByOwnerId(anyInt(), any());
    }

    @Test
    void addComment() {
        Comment commentWithBadAuthor = CommentBuilder.buildComment(1, "comment",
                new Item(),
                UserBuilder.buildUser(99, null, null),
                LocalDateTime.now());

        Comment commentWithBadItem = CommentBuilder.buildComment(2, "comment",
                ItemBuilder.buildItem(99, null, null, true, null, null, null, null, null),
                UserBuilder.buildUser(1, "user", "email"),
                LocalDateTime.now());

        Comment commentWithoutBooking = CommentBuilder.buildComment(2, "comment",
                ItemBuilder.buildItem(2, null, null, true, null, null, null, null, null),
                UserBuilder.buildUser(2, null, null),
                LocalDateTime.now());

        Comment repeatedComment = CommentBuilder.buildComment(3, "comment",
                ItemBuilder.buildItem(3, null, null, true, null, null, null, null, null),
                UserBuilder.buildUser(3, null, null),
                LocalDateTime.now());

        Comment comment = CommentBuilder.buildComment(4, "comment",
                ItemBuilder.buildItem(4, null, null, true, null, null, null, null, null),
                UserBuilder.buildUser(4, null, null),
                LocalDateTime.now());


        when(userRepository.findById(99)).thenReturn(Optional.empty());

        when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(commentWithBadItem.getAuthor())));
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        when(userRepository.findById(2)).thenReturn(Optional.of(userMapper.toUserEntity(commentWithoutBooking.getAuthor())));
        when(itemRepository.findById(2)).thenReturn(Optional.of(itemMapper.toItemEntity(commentWithoutBooking.getItem())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(eq(2),
                eq(2), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(false);

        when(userRepository.findById(3)).thenReturn(Optional.of(userMapper.toUserEntity(repeatedComment.getAuthor())));
        when(itemRepository.findById(3)).thenReturn(Optional.of(itemMapper.toItemEntity(repeatedComment.getItem())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(eq(3),
                eq(3), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.existsByItemIdAndAuthorId(3, 3)).thenReturn(true);

        when(userRepository.findById(4)).thenReturn(Optional.of(userMapper.toUserEntity(repeatedComment.getAuthor())));
        when(itemRepository.findById(4)).thenReturn(Optional.of(itemMapper.toItemEntity(repeatedComment.getItem())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(eq(4),
                eq(4), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.existsByItemIdAndAuthorId(4, 4)).thenReturn(false);
        when(commentRepository.save(any())).thenReturn(commentMapper.toCommentEntity(comment));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.addComment(commentWithBadAuthor));
        verify(commentRepository, never()).save(any());

        assertThrows(DataDoesNotExistsException.class, () -> itemService.addComment(commentWithBadItem));
        verify(commentRepository, never()).save(any());

        assertThrows(WithoutBookingException.class, () -> itemService.addComment(commentWithoutBooking));
        verify(commentRepository, never()).save(any());

        assertThrows(DataAlreadyExistsException.class, () -> itemService.addComment(repeatedComment));
        verify(commentRepository, never()).save(any());

        assertEquals(comment, itemService.addComment(comment));
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void itemShortResponseCoverageTest() {
        ItemShortResponse response = new ItemShortResponse();
        response.setId(1);
        response.setName("name");
        response.setAvailable(true);
        response.setDescription("desc");
        response.setRequestId(2);

    }
}