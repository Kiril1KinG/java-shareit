package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Mapping(target = "text", source = "request.text")
    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "author.id", source = "userId")
    public abstract Comment toComment(CommentRequest request, Integer itemId, Integer userId);

    @Mapping(target = "authorName", source = "author.name")
    public abstract CommentResponse toResponse(Comment comment);

    public abstract CommentEntity toCommentEntity(Comment comment);

    public abstract Comment toComment(CommentEntity entity);

    public Collection<Comment> toComments(Collection<CommentEntity> entities) {
        return entities.stream()
                .map(this::toComment)
                .collect(Collectors.toList());
    }

    public Collection<CommentResponse> toCommentResponses(Collection<Comment> comments) {
        return comments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}