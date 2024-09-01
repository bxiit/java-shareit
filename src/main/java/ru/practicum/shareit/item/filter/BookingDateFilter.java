package ru.practicum.shareit.item.filter;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingDateFilter {
    Booking getBooking(List<Booking> bookings);
}
