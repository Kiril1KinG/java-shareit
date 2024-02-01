package ru.practicum.shareit.exception;

public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
