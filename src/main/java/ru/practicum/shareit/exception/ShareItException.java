package ru.practicum.shareit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ShareItException extends RuntimeException {
    final HttpStatus httpStatus;

    ShareItException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
