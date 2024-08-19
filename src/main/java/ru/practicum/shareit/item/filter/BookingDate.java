package ru.practicum.shareit.item.filter;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public enum BookingDate implements BookingDateFilter {
    LAST {
        @Override
        public LocalDate getBookingDate(List<Booking> bookings) {
            if (bookings.isEmpty()) {
                return null;
            }
            return bookings.stream()
                    .map(Booking::getEnd)
                    .filter(endDate -> endDate.isBefore(Instant.now()))
                    .min(Instant::compareTo)
                    .map(InstantConverter::toLocalDate)
                    .filter(endDate -> !endDate.isEqual(LocalDate.now()))
                    .orElse(null);
        }
    },
    NEXT {
        @Override
        public LocalDate getBookingDate(List<Booking> bookings) {
            if (bookings.isEmpty()) {
                return null;
            }
            return bookings.stream()
                    .map(Booking::getStart)
                    .filter(startDate -> startDate.isAfter(Instant.now()))
                    .min(Instant::compareTo)
                    .map(InstantConverter::toLocalDate)
                    .filter(startDate -> !startDate.isEqual(LocalDate.now()))
                    .orElse(null);
        }
    }
}
