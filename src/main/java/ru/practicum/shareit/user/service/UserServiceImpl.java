package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public User add(User user) {
        log.info("User added: {}", user);
        return mapper.toUser(userRepository.save(mapper.toUserEntity(user)));
    }

    @Override
    public User get(Integer id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new DataDoesNotExistsException(String.format("Get user failed, user with id %d not exists", id)));
        log.info("User received: {}", userEntity);
        return mapper.toUser(userEntity);
    }

    @Override
    public User update(User user) {
        UserEntity modified = userRepository.findById(user.getId()).orElseThrow(
                () -> new DataDoesNotExistsException(String.format("Update user failed, user with id %d not exists",
                        user.getId())));
        if (userRepository.existsByEmailAndIdNot(modified.getEmail(), modified.getId())) {
            throw new DataAlreadyExistsException(
                    String.format("Update user failed, user with email %s already exists", user.getEmail()));
        }
        if ((user.getEmail() != null)) {
            modified.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            modified.setName(user.getName());
        }
        userRepository.save(modified);
        log.info("User updated: {}", modified);
        return mapper.toUser(modified);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
        log.info("User with id {} removed", id);
    }

    @Override
    public Collection<User> getAll() {
        Collection<UserEntity> users = userRepository.findAll();
        log.info("All users received: {}", users);
        return mapper.toUsers(users);
    }
}