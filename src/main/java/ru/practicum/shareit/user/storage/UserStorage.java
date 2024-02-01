package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User add(User user);

    User get(Integer id);

    User update(User user);

    void delete(Integer id);

    boolean contains(Integer id);

    Collection<User> getAll();
}
