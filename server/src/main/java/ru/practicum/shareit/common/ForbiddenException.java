package ru.practicum.shareit.common;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ShareItException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
