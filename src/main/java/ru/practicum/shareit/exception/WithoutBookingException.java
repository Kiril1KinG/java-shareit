package ru.practicum.shareit.exception;

public class WithoutBookingException extends RuntimeException {
    public WithoutBookingException(String message) {
        super(message);
    }
}