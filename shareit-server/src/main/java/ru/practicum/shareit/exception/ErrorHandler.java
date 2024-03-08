package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(DataDoesNotExistsException e) {
        log.info(e.getMessage(), e);
        return Map.of("Data not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(NotOwnerException e) {
        log.info(e.getMessage(), e);
        return Map.of("You not owner", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handle(DataAlreadyExistsException e) {
        log.info(e.getMessage(), e);
        return Map.of("Already exists", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UnknownStateException handle(UnknownStateException e) {
        log.info(e.getMessage(), e);
        return e;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(RepeatedRequestException e) {
        log.info(e.getMessage(), e);
        return Map.of("Repeated request", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(WithoutBookingException e) {
        log.info(e.getMessage(), e);
        return Map.of("Bad request", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(NotAvailableException e) {
        log.info(e.getMessage(), e);
        return Map.of("Bad request", e.getMessage());
    }

}