package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemShortResponse {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
