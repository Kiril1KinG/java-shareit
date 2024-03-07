package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.classBuilder.TestCommentProvider;
import ru.practicum.shareit.classBuilder.TestItemProvider;
import ru.practicum.shareit.classBuilder.TestUserProvider;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        user = TestUserProvider.buildUserEntity(null, "user", "user@yandex.ru");
        user = userRepository.save(user);

        item = TestItemProvider.provideItemEntity(null, "Дрель", "Проводная дрель", true, user, null);
        item = itemRepository.save(item);

        comment = TestCommentProvider.provideCommentEntity(null, "text", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @Test
    void findAllByItemId() {
        assertEquals(List.of(comment), commentRepository.findAllByItemId(item.getId()));
    }

    @Test
    void existsByItemIdAndAuthorId() {
        assertTrue(commentRepository.existsByItemIdAndAuthorId(item.getId(), user.getId()));
        assertFalse(commentRepository.existsByItemIdAndAuthorId(99, user.getId()));
    }
}
