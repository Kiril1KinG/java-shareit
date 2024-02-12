package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User add(User user);

    User get(Integer id);

    User update(User user);

    void delete(Integer id);

    Collection<User> getAll();
}