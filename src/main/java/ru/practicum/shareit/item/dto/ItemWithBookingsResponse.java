package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.util.List;


@Getter
@Setter
public class ItemWithBookingsResponse extends ItemResponse {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShort lastBooking;
    private BookingShort nextBooking;
    private List<CommentResponse> comments;
}
