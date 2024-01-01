package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestRequest {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
