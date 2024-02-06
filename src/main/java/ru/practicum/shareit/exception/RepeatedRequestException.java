package ru.practicum.shareit.exception;

public class RepeatedRequestException extends RuntimeException {
    public RepeatedRequestException(String message) {
        super(message);
    }
}
