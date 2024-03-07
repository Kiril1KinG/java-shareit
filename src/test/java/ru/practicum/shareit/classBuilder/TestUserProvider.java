package ru.practicum.shareit.classBuilder;

import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;

public class TestUserProvider {

    public static User buildUser(Integer id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public static UserEntity buildUserEntity(Integer id, String name, String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setName(name);
        userEntity.setEmail(email);
        return userEntity;
    }
}
