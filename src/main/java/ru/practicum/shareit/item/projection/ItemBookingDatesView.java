package ru.practicum.shareit.item.projection;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;

public interface ItemBookingDatesView {
    Item getItem();

    Booking getLastBooking();

    Booking getNextBooking();
}
