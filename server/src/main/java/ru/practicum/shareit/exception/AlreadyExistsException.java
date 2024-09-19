package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ShareItException {
    public AlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
