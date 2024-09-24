package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record NewBookingRequest(LocalDateTime start,
                                LocalDateTime end,
                                Long itemId) {
}
