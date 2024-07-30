package ru.practicum.shareit.advice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {


    public static final Object[] EMPTY_ARGS = new Object[0];
    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(
            MethodArgumentNotValidException e
    ) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String errorMessage = errors.stream()
                .map(ObjectError::getDefaultMessage)
                .map(message -> messageSource.getMessage(
                        message, EMPTY_ARGS, message, LocaleContextHolder.getLocale()
                ))
                .collect(Collectors.joining(", "));
        return ProblemDetail.forStatusAndDetail(e.getStatusCode(), errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleConstraintViolationException(
            ConstraintViolationException e
    ) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessageTemplate)
                .map(message -> messageSource.getMessage(
                        message, EMPTY_ARGS, message, LocaleContextHolder.getLocale()
                ))
                .collect(Collectors.joining(", "));
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(CONFLICT)
    public ProblemDetail handleAlreadyExistsException(AlreadyExistsException e) {
        String message = messageSource.getMessage(e.getMessage(), EMPTY_ARGS, e.getMessage(), LocaleContextHolder.getLocale());
        return ProblemDetail.forStatusAndDetail(CONFLICT, message);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ProblemDetail handleNotFoundException(NotFoundException e) {
        String message = messageSource.getMessage(e.getMessage(), EMPTY_ARGS, e.getMessage(), LocaleContextHolder.getLocale());
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, message);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(FORBIDDEN)
    public ProblemDetail handleForbiddenException(ForbiddenException e) {
        String message = messageSource.getMessage(e.getMessage(), EMPTY_ARGS, e.getMessage(), LocaleContextHolder.getLocale());
        return ProblemDetail.forStatusAndDetail(FORBIDDEN, message);
    }
}
