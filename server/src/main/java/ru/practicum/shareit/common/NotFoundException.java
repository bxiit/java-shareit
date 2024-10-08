package ru.practicum.shareit.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends ShareItException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
