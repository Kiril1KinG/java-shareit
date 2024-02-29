package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class BookingRequest {

    @NotNull
    private Integer itemId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
}