package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemShortResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestResponseWithItems {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortResponse> items;
}
