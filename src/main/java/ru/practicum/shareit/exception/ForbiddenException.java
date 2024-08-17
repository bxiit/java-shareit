package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ShareItException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
