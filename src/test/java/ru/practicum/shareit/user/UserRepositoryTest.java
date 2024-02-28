package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.storage.UserRepository;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void existsByEmailAndIdNot() {
        UserEntity user = new UserEntity(1, "user", "email@yandex.ru");
        user = repository.save(user);

        Assertions.assertTrue(repository.existsByEmailAndIdNot("email@yandex.ru", 99));
        Assertions.assertFalse(repository.existsByEmailAndIdNot("email@yandex.ru", user.getId()));

    }
}