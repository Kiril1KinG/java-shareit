package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);

    User toUser(UserUpdateRequest request);

    UserResponse toResponse(User user);
}
