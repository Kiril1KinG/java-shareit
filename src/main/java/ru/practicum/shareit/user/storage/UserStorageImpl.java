package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage{
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public User add(User user) {
        user.setId(id);
        users.put(id++, user);
        return user;
    }

    @Override
    public User get(Integer id) {
        return users.get(id);
    }

    @Override
    public User update(Integer id, User user) {
        users.put(id, user);
        return users.get(user.getId());
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }

    @Override
    public boolean contains(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }
}
