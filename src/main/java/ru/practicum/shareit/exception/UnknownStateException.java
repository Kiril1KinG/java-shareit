package ru.practicum.shareit.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnknownStateException extends RuntimeException{

    private String error;

    public UnknownStateException(String message) {
        super(message);
        this.error = message;
    }
}
