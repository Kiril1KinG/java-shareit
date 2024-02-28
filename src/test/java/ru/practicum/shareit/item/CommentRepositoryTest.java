package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;
    private ItemEntity item;
    private CommentEntity comment;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();

        user = new UserEntity(null, "user", "user@yandex.ru");
        user = userRepository.save(user);

        item = new ItemEntity(null, "Дрель", "Проводная дрель", true, user, null);
        item = itemRepository.save(item);

        comment = new CommentEntity(null, "text", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @Test
    void findAllByItemId() {
        Assertions.assertEquals(List.of(comment), commentRepository.findAllByItemId(item.getId()));
    }

    @Test
    void existsByItemIdAndAuthorId() {
        Assertions.assertTrue(commentRepository.existsByItemIdAndAuthorId(item.getId(), user.getId()));
        Assertions.assertFalse(commentRepository.existsByItemIdAndAuthorId(99, user.getId()));
    }
}
