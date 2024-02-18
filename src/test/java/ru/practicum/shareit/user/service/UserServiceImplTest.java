package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void add() {
        User user = new User(null, "name", "email");
        User expected = new User(1, "name", "email");

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userMapper.toUserEntity(expected));

        Assertions.assertEquals(expected, userService.add(user));
    }

    @Test
    void get() {
        User expected = new User(1, "name", "email");

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(expected)));
        Mockito.when(userRepository.findById(99)).thenReturn(Optional.empty());

        Assertions.assertEquals(expected, userService.get(1));
        Assertions.assertThrows(DataDoesNotExistsException.class, () -> userService.get(99));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1);
    }

    @Test
    void update() {
        User user = new User(1, "name", "email");
        User user2 = new User(1, "update user", "update email");
        User userWithNulls = new User(1, null, null);
        User userWithIncorrectId = new User(2, "update user", "update email");
        User userWithDuplicateEmail = new User(3, "update user", "email");

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(userMapper.toUserEntity(user)));
        Mockito.when(userRepository.findById(3)).thenReturn(Optional.of(userMapper.toUserEntity(userWithDuplicateEmail)));
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.empty());
        Mockito.when(userRepository.existsByEmailAndIdNot(userWithDuplicateEmail.getEmail(), userWithDuplicateEmail.getId()))
                .thenReturn(true);
        Mockito.when(userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId())).thenReturn(false);
        Mockito.when(userRepository.existsByEmailAndIdNot(user2.getEmail(), user2.getId())).thenReturn(false);


        Assertions.assertEquals(user2, userService.update(user2));
        Assertions.assertEquals(user, userService.update(userWithNulls));
        Assertions.assertThrows(DataDoesNotExistsException.class, () -> userService.update(userWithIncorrectId));
        Assertions.assertThrows(DataAlreadyExistsException.class, () -> userService.update(userWithDuplicateEmail));

        Mockito.verify(userRepository, Mockito.never()).save(userMapper.toUserEntity(userWithDuplicateEmail));
        Mockito.verify(userRepository, Mockito.never()).save(userMapper.toUserEntity(userWithIncorrectId));
    }

    @Test
    void delete() {
        userService.delete(1);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    void getAll() {
        User user = new User(1, "name", "email");
        User user2 = new User(1, "user2", "email2");

        List<UserEntity> users = Stream.of(user, user2)
                .map(userMapper::toUserEntity)
                .collect(Collectors.toList());
        Collection<User> expected = List.of(user, user2);

        Mockito.when(userRepository.findAll()).thenReturn(users);

        Assertions.assertEquals(expected, userService.getAll());
    }
}
