package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
public class BookingRequest {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User broker;
    private String status;
}
