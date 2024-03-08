package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.PaginationParamsException;

public class PaginationValidator {

    public static void validatePaginationParams(Integer from, Integer size) {
        if ((from == null && size != null) || (size == null && from != null)) {
            throw new PaginationParamsException("One of pagination params cannot be null");
        }
    }
}
