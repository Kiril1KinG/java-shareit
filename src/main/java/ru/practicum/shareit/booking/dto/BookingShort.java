package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingShort {

    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}