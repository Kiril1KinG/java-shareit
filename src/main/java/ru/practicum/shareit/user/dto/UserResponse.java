package ru.practicum.shareit.user.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
}
