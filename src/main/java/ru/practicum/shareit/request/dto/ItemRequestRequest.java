package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestRequest {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
