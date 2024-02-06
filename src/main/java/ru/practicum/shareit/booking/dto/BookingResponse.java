package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemResponse item;
    private UserResponse booker;
    private String status;
}
