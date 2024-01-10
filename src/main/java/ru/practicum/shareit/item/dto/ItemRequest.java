package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private String request;
}
