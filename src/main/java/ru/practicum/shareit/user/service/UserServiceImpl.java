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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public User add(User user) {
//        if (userRepository.findAll().stream().map(UserEntity::getEmail).anyMatch(p -> p.equals(user.getEmail()))) {
//            throw new DataAlreadyExistsException(
//                    String.format("Add user failed, user with email %s already exists", user.getEmail()));
//        }
        log.info("User added: {}", user);
        return mapper.toUser(userRepository.save(mapper.toUserEntity(user)));
    }

    @Override
    public User get(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new DataDoesNotExistsException(
                    String.format("Get user failed, user with id %d not exists", id));
        }
        Optional<UserEntity> userEntity = userRepository.findById(id);
        log.info("User received: {}", userEntity);
        return mapper.toUser(userEntity.get());
    }

    @Override
    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException(
                    String.format("Update user failed, user with id %d not exists", user.getId()));
        }
        Optional<UserEntity> modified = userRepository.findById(user.getId());
        List<UserEntity> allUsersWithoutThis = new ArrayList<>(userRepository.findAll());
        allUsersWithoutThis.remove(modified.get());
        if (allUsersWithoutThis.stream().map(UserEntity::getEmail).anyMatch(p -> p.equals(user.getEmail()))) {
            throw new DataAlreadyExistsException(
                    String.format("Update user failed, user with email %s already exists", user.getEmail()));
        }
        if ((user.getEmail() != null)) {
            modified.get().setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            modified.get().setName(user.getName());
        }
        userRepository.save(modified.get());
        log.info("User updated: {}", modified);
        return mapper.toUser(modified.get());
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
