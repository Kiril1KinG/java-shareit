package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.classBuilder.TestUserProvider;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void existsByEmailAndIdNot() {
        UserEntity user = TestUserProvider.buildUserEntity(1, "user", "email@yandex.ru");
        user = repository.save(user);

        assertTrue(repository.existsByEmailAndIdNot("email@yandex.ru", 99));
        assertFalse(repository.existsByEmailAndIdNot("email@yandex.ru", user.getId()));

    }
}