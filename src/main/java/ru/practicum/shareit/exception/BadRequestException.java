package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ShareItException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
