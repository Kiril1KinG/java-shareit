package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserStorage userStorage;

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User get(Integer id) {
        if (!userStorage.contains(id)) {
            throw new RuntimeException(String.format("Get user failed, user with id %d not exists", id));
        }
        return userStorage.get(id);
    }

    @Override
    public User update(Integer id, User user) {
        if (!userStorage.contains(id)) {
            throw new RuntimeException(String.format("Update user failed, user with id %d not exists", id));
        }
        User modified = userStorage.get(id);
        modified.setEmail(user.getEmail());
        modified.setName(user.getName());
        userStorage.update(id, modified);
        return modified;
    }

    @Override
    public void delete(Integer id) {
        userStorage.delete(id);
    }

    @Override
    public Collection<User> getAll(){
        return userStorage.getAll();
    }
}
