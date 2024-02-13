package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequest {

    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<Item> items;
}