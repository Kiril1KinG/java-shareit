package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.classBuilder.ItemBuilder;
import ru.practicum.shareit.classBuilder.ItemRequestBuilder;
import ru.practicum.shareit.classBuilder.UserBuilder;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user1;
    private UserEntity user2;
    private ItemRequestEntity itemRequest1;
    private ItemEntity item1;
    private ItemEntity item2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();

        user1 = UserBuilder.buildUserEntity(null, "name", "email@yandex.ru");
        user1 = userRepository.save(user1);
        user2 = UserBuilder.buildUserEntity(null, "other name", "otherEmail@yandex.ru");
        user2 = userRepository.save(user2);

        itemRequest1 = ItemRequestBuilder.buildItemRequestEntity(null, "Нужени миксер",
                user1, LocalDateTime.now());
        itemRequest1 = itemRequestRepository.save(itemRequest1);

        item1 = ItemBuilder.buildItemEntity(null, "Дрель", "Проводная дрель", true, user1, null);
        item1 = itemRepository.save(item1);
        item2 = ItemBuilder.buildItemEntity(null, "Миксер", "Кухонный миксер", true, user2, itemRequest1);
        item2 = itemRepository.save(item2);
    }

    @Test
    void findAllByOwnerId() {
        assertEquals(List.of(item1),
                itemRepository.findAllByOwnerId(user1.getId(), Pageable.ofSize(10)).getContent());
    }

    @Test
    void search() {
        assertEquals(List.of(item1),
                itemRepository.search("дрель", Pageable.ofSize(10)).getContent());
    }


    @Test
    void findAllByRequestRequestorId() {
        assertEquals(List.of(item2), itemRepository.findAllByRequestRequestorId(user1.getId()));
    }

    @Test
    void findAllByRequestId() {
        assertEquals(List.of(item2), itemRepository.findAllByRequestId(itemRequest1.getId()));
    }
}