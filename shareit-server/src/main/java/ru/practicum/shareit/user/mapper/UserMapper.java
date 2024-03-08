package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);

    User toUser(UserUpdateRequest request);

    User toUser(UserEntity userEntity);

    UserResponse toResponse(User user);

    UserEntity toUserEntity(User user);

    Collection<User> toUsers(Collection<UserEntity> userEntities);
}