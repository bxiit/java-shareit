package ru.practicum.shareit.item.filter;

import ru.practicum.shareit.booking.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingDateFilter {
    LocalDate getBookingDate(List<Booking> bookings);
}
