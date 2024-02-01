package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserCreateRequest {
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;
}
