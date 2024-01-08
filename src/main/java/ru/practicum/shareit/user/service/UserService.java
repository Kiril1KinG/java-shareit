package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public User add(User user) {
        user.setId(id);
        users.put(id++, user);
        return user;
    }

    public User get(int id) {
        return users.get(id);
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public void delete(int id) {
        users.remove(id);
    }
}
