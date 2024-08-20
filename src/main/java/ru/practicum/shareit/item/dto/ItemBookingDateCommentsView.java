package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDate;

public interface ItemBookingDateCommentsView {
    Item getItem();
    LocalDate getLastBookingDate();
    LocalDate getNextBookingDate();
}
