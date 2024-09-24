package ru.practicum.shareit.common;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ShareItException {
    public AlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
