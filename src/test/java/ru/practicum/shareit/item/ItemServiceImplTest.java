package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import ru.practicum.shareit.item.storage.BookingRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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


        when(itemRequestRepository.findById(99)).thenReturn(Optional.empty());
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequestMapper.toEntity(item.getRequest())));
        when(itemRequestRepository.findById(2)).thenReturn(Optional.of(itemRequestMapper.toEntity(item2.getRequest())));
        when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(item2.getOwner())));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.add(1, itemWithIncorrectRequestId));
        verify(itemRepository, Mockito.never()).save(itemMapper.toItemEntity(itemWithIncorrectRequestId));
        assertThrows(DataDoesNotExistsException.class, () -> itemService.add(99, item));
        verify(itemRepository, Mockito.never()).save(itemMapper.toItemEntity(item));
        assertEquals(item2, itemService.add(1, item2));
        verify(itemRepository, Mockito.times(1)).save(item2Entity);
    }

    @Test
    void get() {
        Item item = new Item(1, "item", "desc", true,
                new User(1, "user", "email"),
                new ItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStart(LocalDateTime.now());


        when(itemRepository.findById(99)).thenReturn(Optional.empty());
        when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntity));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastAndNextBookingByItemId(Mockito.anyInt(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(bookingEntity));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.get(99, 1));
        assertEquals(item, itemService.get(1, 1));
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


        when(itemRepository.findById(99)).thenReturn(Optional.empty());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(itemEntity));
        when(userRepository.existsById(99)).thenReturn(false);
        when(userRepository.existsById(3)).thenReturn(true);
        when(itemRepository.findById(2)).thenReturn(Optional.of(itemForUpdateEntity));
        when(userRepository.existsById(2)).thenReturn(true);


        assertThrows(DataDoesNotExistsException.class, () -> itemService.update(1, empty));
        verify(itemRepository, Mockito.never()).save(emptyEntity);

        assertThrows(DataDoesNotExistsException.class, () -> itemService.update(99, item));

        assertThrows(DataDoesNotExistsException.class, () -> itemService.update(3, item));
        verify(itemRepository, Mockito.never()).save(itemEntity);

        assertEquals(update, itemService.update(2, update));
        verify(itemRepository, Mockito.times(1)).save(updateEntity);
    }

    @Test
    void delete() {
        Item itemWithIncorrectOwnerId = new Item(1, "item", "desc", true,
                new User(99, "user", "email"),
                new ItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntityWithIncorrectOwnerId = itemMapper.toItemEntity(itemWithIncorrectOwnerId);

        Item item = new Item(2, "item", "desc", true,
                new User(2, "user", "email"),
                new ItemRequest(2, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);


        when(itemRepository.findById(99)).thenReturn(Optional.empty());
        when(itemRepository.findById(1)).thenReturn(Optional.of(itemEntityWithIncorrectOwnerId));
        when(itemRepository.findById(2)).thenReturn(Optional.of(itemEntity));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.delete(1, 99));
        verify(itemRepository, Mockito.never()).deleteById(1);

        assertThrows(DataDoesNotExistsException.class, () -> itemService.delete(1, 1));
        verify(itemRepository, Mockito.never()).deleteById(1);

        itemService.delete(2, 2);
        verify(itemRepository, Mockito.times(1)).deleteById(2);
    }

    @Test
    void search() {
        Item item = new Item(1, "item", "desc", true,
                new User(1, "user", "email"),
                new ItemRequest(1, null, null, null, null),
                null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);
        Page<ItemEntity> page = new PageImpl<>(List.of(itemEntity));


        when(itemRepository.search(Mockito.anyString(), Mockito.any())).thenReturn(page);


        assertEquals(Collections.emptyList(), itemService.search(" ", 0, 2));
        verify(itemRepository, Mockito.never()).search(Mockito.anyString(), Mockito.any());

        assertThrows(PaginationParamsException.class, () -> itemService.search("text", 0, null));
        verify(itemRepository, Mockito.never()).search(Mockito.anyString(), Mockito.any());

        assertEquals(List.of(item), itemService.search("search", 0, 1));
        verify(itemRepository, Mockito.times(1)).search(Mockito.anyString(), Mockito.any());
    }

    @Test
    void getByOwnerId() {
        Item item = new Item(1, "item", "desc", true,
                new User(1, "user", "email"),
                null, null, null, null);
        ItemEntity itemEntity = itemMapper.toItemEntity(item);
        Page<ItemEntity> page = new PageImpl<>(List.of(itemEntity));


        when(itemRepository.findAllByOwnerId(Mockito.anyInt(), Mockito.any())).thenReturn(page);


        assertThrows(PaginationParamsException.class, () -> itemService.getByOwnerId(99, 0, null));
        verify(itemRepository, Mockito.never()).findAllByOwnerId(Mockito.anyInt(), Mockito.any());

        assertEquals(List.of(item), itemService.getByOwnerId(1, 0, 1));
        verify(itemRepository, Mockito.times(1)).findAllByOwnerId(Mockito.anyInt(), Mockito.any());
    }

    @Test
    void addComment() {
        Comment commentWithBadAuthor = new Comment(1, "comment",
                new Item(),
                new User(99, null, null),
                LocalDateTime.now());

        Comment commentWithBadItem = new Comment(2, "comment",
                new Item(99, null, null, true, null, null, null, null, null),
                new User(1, "user", "email"),
                LocalDateTime.now());

        Comment commentWithoutBooking = new Comment(2, "comment",
                new Item(2, null, null, true, null, null, null, null, null),
                new User(2, null, null),
                LocalDateTime.now());

        Comment repeatedComment = new Comment(3, "comment",
                new Item(3, null, null, true, null, null, null, null, null),
                new User(3, null, null),
                LocalDateTime.now());

        Comment comment = new Comment(4, "comment",
                new Item(4, null, null, true, null, null, null, null, null),
                new User(4, null, null),
                LocalDateTime.now());


        when(userRepository.findById(99)).thenReturn(Optional.empty());

        when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(commentWithBadItem.getAuthor())));
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        when(userRepository.findById(2)).thenReturn(Optional.of(userMapper.toUserEntity(commentWithoutBooking.getAuthor())));
        when(itemRepository.findById(2)).thenReturn(Optional.of(itemMapper.toItemEntity(commentWithoutBooking.getItem())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(Mockito.eq(2),
                Mockito.eq(2), Mockito.eq(BookingStatus.APPROVED), Mockito.any(LocalDateTime.class))).thenReturn(false);

        when(userRepository.findById(3)).thenReturn(Optional.of(userMapper.toUserEntity(repeatedComment.getAuthor())));
        when(itemRepository.findById(3)).thenReturn(Optional.of(itemMapper.toItemEntity(repeatedComment.getItem())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(Mockito.eq(3),
                Mockito.eq(3), Mockito.eq(BookingStatus.APPROVED), Mockito.any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.existsByItemIdAndAuthorId(3, 3)).thenReturn(true);

        when(userRepository.findById(4)).thenReturn(Optional.of(userMapper.toUserEntity(repeatedComment.getAuthor())));
        when(itemRepository.findById(4)).thenReturn(Optional.of(itemMapper.toItemEntity(repeatedComment.getItem())));
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(Mockito.eq(4),
                Mockito.eq(4), Mockito.eq(BookingStatus.APPROVED), Mockito.any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.existsByItemIdAndAuthorId(4, 4)).thenReturn(false);
        when(commentRepository.save(Mockito.any())).thenReturn(commentMapper.toCommentEntity(comment));


        assertThrows(DataDoesNotExistsException.class, () -> itemService.addComment(commentWithBadAuthor));
        verify(commentRepository, Mockito.never()).save(Mockito.any());

        assertThrows(DataDoesNotExistsException.class, () -> itemService.addComment(commentWithBadItem));
        verify(commentRepository, Mockito.never()).save(Mockito.any());

        assertThrows(WithoutBookingException.class, () -> itemService.addComment(commentWithoutBooking));
        verify(commentRepository, Mockito.never()).save(Mockito.any());

        assertThrows(DataAlreadyExistsException.class, () -> itemService.addComment(repeatedComment));
        verify(commentRepository, Mockito.never()).save(Mockito.any());

        assertEquals(comment, itemService.addComment(comment));
        verify(commentRepository, Mockito.times(1)).save(Mockito.any());
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