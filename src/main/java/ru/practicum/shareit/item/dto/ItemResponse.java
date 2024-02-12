package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemResponse {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentResponse> comments;
}