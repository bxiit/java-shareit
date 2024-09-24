package ru.practicum.shareit.common.advice;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.common.ShareItException;

import java.beans.PropertyEditorSupport;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    public static final Object[] EMPTY_ARGS = new Object[0];
    private final MessageSource messageSource;

    @ExceptionHandler(ShareItException.class)
    public ProblemDetail handleCommonException(ShareItException e) {
        String message = messageSource.getMessage(e.getMessage(), EMPTY_ARGS, e.getMessage(), LocaleContextHolder.getLocale());
        return ProblemDetail.forStatusAndDetail(e.getHttpStatus(), message);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(State.class, EnumPropertyEditor.forEnum(State.class));
    }

    @AllArgsConstructor
    private static class EnumPropertyEditor<T extends Enum<T>> extends PropertyEditorSupport {
        private final Class<T> enumClass;

        public static <T extends Enum<T>> EnumPropertyEditor<T> forEnum(final Class<T> enumClass) {
            return new EnumPropertyEditor<>(enumClass);
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(Enum.valueOf(enumClass, text.toUpperCase()));
        }
    }
}
