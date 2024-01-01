package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingResponse {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User broker;
    private String status;
}
