package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.CommentEntity;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    Collection<CommentEntity> findAllByItemId(Integer itemId);

    boolean existsByItemIdAndAuthorId(Integer itemId, Integer authorId);
}