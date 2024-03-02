package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user1;
    private UserEntity user2;
    private ItemRequestEntity itemRequest1;
    private ItemRequestEntity itemRequest2;
    private ItemRequestEntity itemRequest3;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        userRepository.deleteAll();

        user1 = new UserEntity(null, "name", "email@yandex.ru");
        user1 = userRepository.save(user1);
        user2 = new UserEntity(null, "other name", "otherEmail@yandex.ru");
        user2 = userRepository.save(user2);

        itemRequest1 = new ItemRequestEntity(null, "desc", user1, LocalDateTime.now());
        itemRequest1 = repository.save(itemRequest1);
        itemRequest2 = new ItemRequestEntity(null, "desc", user1, LocalDateTime.now());
        itemRequest2 = repository.save(itemRequest2);
        itemRequest3 = new ItemRequestEntity(null, "desc", user2, LocalDateTime.now());
        itemRequest3 = repository.save(itemRequest3);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        assertEquals(List.of(itemRequest2, itemRequest1), repository.findAllByRequestorIdOrderByCreatedDesc(user1.getId()));
    }

    @Test
    void findAllWithoutRequestor() {
        assertEquals(List.of(itemRequest3),
                repository.findAllWithoutRequestor(user1.getId(), Pageable.ofSize(10)).getContent());
    }
}