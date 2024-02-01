package ru.practicum.shareit.exception;

public class DataDoesNotExistsException extends RuntimeException {
    public DataDoesNotExistsException(String message) {
        super(message);
    }
}
