package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}