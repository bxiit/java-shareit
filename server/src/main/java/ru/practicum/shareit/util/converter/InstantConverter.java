package ru.practicum.shareit.util.converter;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.time.ZoneOffset.UTC;

@UtilityClass
public class InstantConverter {
    private static final ZoneOffset ZONE_OFFSET = UTC;

    public LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZONE_OFFSET);
    }

    public Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET);
    }
}
