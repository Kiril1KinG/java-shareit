package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestResponse {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
