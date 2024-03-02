package ru.practicum.shareit.classBuilder;

import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentBuilder {

    public static Comment buildComment(Integer id, String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }

    public static CommentEntity buildCommentEntity(Integer id, String text, ItemEntity item, UserEntity author, LocalDateTime created) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setId(id);
        commentEntity.setText(text);
        commentEntity.setItem(item);
        commentEntity.setAuthor(author);
        commentEntity.setCreated(created);
        return commentEntity;
    }
}
