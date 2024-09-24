package ru.practicum.shareit.common;

import org.springframework.http.HttpStatus;

public class UnavailableItemException extends ShareItException {
    public UnavailableItemException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
