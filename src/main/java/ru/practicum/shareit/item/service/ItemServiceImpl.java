package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.exception.WithoutBookingException;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public Item add(int userId, Item item) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new DataDoesNotExistsException(String.format("Add item failed, user with id %d npt exists",
                        userId)));
        item.setOwner(userMapper.toUser(userEntity));
        ItemEntity res = itemMapper.toItemEntity(item);
        itemRepository.save(res);
        log.info("Item added: {}", res);
        return itemMapper.toItem(res);
    }

    @Override
    public Item get(int id, Integer userId) {
        ItemEntity itemEntity = itemRepository.findById(id).orElseThrow(
                () -> new DataDoesNotExistsException(String.format("Get item by id failed, item with %d not exists", id)));
        Item item = itemMapper.toItem(itemEntity);
        if (item.getOwner().getId().equals(userId)) {
            addBookingsToItems(Collections.singleton(item));
        }
        addCommentsToItems(Collections.singleton(item));
        log.info("Item received: {}", item);
        return item;
    }

    @Override
    public Item update(int userId, Item item) {
        ItemEntity itemEntity = itemRepository.findById(item.getId()).orElseThrow(
                () -> new DataDoesNotExistsException(String.format("Update item failed, item with %d not exists",
                        item.getId())));
        if (!userRepository.existsById(userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, user with %d not exists", userId));
        }
        if (itemEntity.getOwner().getId() != userId) {
            throw new DataDoesNotExistsException(
                    String.format("Update item failed, user with %d not owner", userId));
        }

        if (item.getName() != null) {
            itemEntity.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemEntity.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemEntity.setAvailable(item.getAvailable());
        }
        itemRepository.save(itemEntity);
        log.info("Item updated: {}", itemEntity);
        return itemMapper.toItem(itemEntity);
    }

    @Override
    public void delete(int userId, int id) {
        ItemEntity itemEntity = itemRepository.findById(id).orElseThrow(
                () -> new DataDoesNotExistsException(String.format("Delete item failed, item with id %d not exists", id)));
        if ((itemEntity.getOwner().getId() != userId)) {
            throw new DataDoesNotExistsException(
                    String.format("Delete item failed, user with %d not owner", userId));
        }
        itemRepository.deleteById(id);
        log.info("Item with id {} deleted", id);
    }


    @Override
    public Collection<Item> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<ItemEntity> items = itemRepository.search(text);
        log.info("Item search by request \"{}\" received: {}", text, items);
        return itemMapper.toItems(items);
    }

    @Override
    public Collection<Item> getByOwnerId(int userId) {
        Collection<Item> items = itemRepository.findUsersByOwnerIdOrderByIdAsc(userId).stream()
                .map(itemMapper::toItem)
                .collect(Collectors.toList());
        addBookingsToItems(items);
        addCommentsToItems(items);
        log.info("Items for owner received: {}", items);
        return items;
    }

    @Override
    public Comment addComment(Comment comment) {
        UserEntity userEntity = userRepository.findById(comment.getAuthor().getId()).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Add comment failed, user with id %d not exists", comment.getAuthor().getId())));

        ItemEntity itemEntity = itemRepository.findById(comment.getItem().getId()).orElseThrow(
                () -> new DataDoesNotExistsException(
                        String.format("Add comment failed, item with id %d not exists", comment.getAuthor().getId())));

        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(comment.getItem().getId(),
                comment.getAuthor().getId(), BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new WithoutBookingException(
                    String.format("Add comment failed, user with id %d did not booked this thing",
                            comment.getAuthor().getId()));
        }

        if (commentRepository.existsByItemIdAndAuthorId(comment.getItem().getId(), comment.getAuthor().getId())) {
            throw new DataAlreadyExistsException("Add comment failed, comment already exists");
        }

        CommentEntity commentEntity = commentRepository.save(commentMapper.toCommentEntity(comment));
        commentEntity.setItem(itemEntity);
        commentEntity.setAuthor(userEntity);
        return commentMapper.toComment(commentEntity);
    }

    private void addBookingsToItems(Collection<Item> items) {
        for (Item item : items) {
            ArrayList<BookingEntity> bookingEntities = new ArrayList<>(bookingRepository.findLastAndNextBookingByItemId(
                    item.getId(), LocalDateTime.now()));
            if (bookingEntities.size() == 2) {
                item.setLastBooking(bookingMapper.toBooking(bookingEntities.get(0)));
                item.setNextBooking(bookingMapper.toBooking(bookingEntities.get(1)));
            } else if (bookingEntities.size() == 1) {
                if (bookingEntities.get(0).getStart().isBefore(LocalDateTime.now())) {
                    item.setLastBooking(bookingMapper.toBooking(bookingEntities.get(0)));
                } else {
                    item.setNextBooking(bookingMapper.toBooking(bookingEntities.get(0)));
                }
            }
        }
    }

    private void addCommentsToItems(Collection<Item> items) {
        for (Item item : items) {
            item.setComments(List.copyOf(commentMapper.toComments(commentRepository.findAllByItemId(item.getId()))));
        }
    }
}