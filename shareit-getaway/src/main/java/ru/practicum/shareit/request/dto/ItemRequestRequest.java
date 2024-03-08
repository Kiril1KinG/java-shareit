package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestRequest {

    @NotNull
    @NotBlank
    @NotEmpty
    private String description;

    private LocalDateTime created;
}