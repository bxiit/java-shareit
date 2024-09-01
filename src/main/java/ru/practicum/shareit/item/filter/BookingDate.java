package ru.practicum.shareit.item.filter;

import ru.practicum.shareit.booking.Booking;

import java.util.Comparator;
import java.util.List;

public enum BookingDate implements BookingDateFilter {
    LAST {
        @Override
        public Booking getBooking(List<Booking> bookings) {
            return bookings.stream()
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }
    },
    NEXT {
        @Override
        public Booking getBooking(List<Booking> bookings) {
            return bookings.stream()
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }
    }
}
