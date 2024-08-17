package ru.practicum.shareit.advice;

import jakarta.persistence.EntityNotFoundException;
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
import ru.practicum.shareit.exception.ShareItException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
                .filter(Objects::nonNull)
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

    @ExceptionHandler(ShareItException.class)
    public ProblemDetail handleCommonException(ShareItException e) {
        String message = messageSource.getMessage(e.getMessage(), EMPTY_ARGS, e.getMessage(), LocaleContextHolder.getLocale());
        return ProblemDetail.forStatusAndDetail(e.getHttpStatus(), message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException e) {
        String errorMessage = e.getMessage();
        errorMessage = errorMessage.replaceAll("ru\\.practicum\\.shareit\\.[^.]+\\.", "");
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, errorMessage);
    }
}
