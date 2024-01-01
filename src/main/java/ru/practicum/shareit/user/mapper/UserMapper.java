package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

public interface UserMapper {

    User toUser(UserRequest dto);

    UserResponse toResponse(User user);
}
