package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User add(User user) {
        if (userStorage.getAll().stream().map(User::getEmail).anyMatch(p -> p.equals(user.getEmail()))) {
            throw new DataAlreadyExistsException(String.format("Add user failed, user with email %s already exists",
                    user.getEmail()));
        }
        log.info("User added: {}", user);
        return userStorage.add(user);
    }

    @Override
    public User get(Integer id) {
        if (!userStorage.contains(id)) {
            throw new RuntimeException(String.format("Get user failed, user with id %d not exists", id));
        }
        User user = userStorage.get(id);
        log.info("User received: {}", user);
        return user;
    }

    @Override
    public User update(Integer id, User user) {
        if (!userStorage.contains(id)) {
            throw new RuntimeException(String.format("Update user failed, user with id %d not exists", id));
        }
        User modified = userStorage.get(id);
        List<User> allUsersWithoutThis = new ArrayList<>(userStorage.getAll());
        allUsersWithoutThis.remove(modified);
        if (allUsersWithoutThis.stream().map(User::getEmail).anyMatch(p -> p.equals(user.getEmail()))) {
            throw new DataAlreadyExistsException(String.format("Update user failed, user with email %s already exists",
                    user.getEmail()));
        }
        if ((user.getEmail() != null)) {
            modified.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            modified.setName(user.getName());
        }
        userStorage.update(id, modified);
        log.info("User updated: {}", modified);
        return modified;
    }

    @Override
    public void delete(Integer id) {
        userStorage.delete(id);
        log.info("User with id {} removed", id);
    }

    @Override
    public Collection<User> getAll() {
        Collection<User> users = userStorage.getAll();
        log.info("All users received: {}", users);
        return users;
    }
}
