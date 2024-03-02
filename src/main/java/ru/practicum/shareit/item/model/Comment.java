package ru.practicum.shareit.item.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Comment {

    private Integer id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created = LocalDateTime.now();
}