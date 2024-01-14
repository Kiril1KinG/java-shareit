package ru.practicum.shareit.user.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class User {
    private Integer id;
    private String name;
    private String email;

}
