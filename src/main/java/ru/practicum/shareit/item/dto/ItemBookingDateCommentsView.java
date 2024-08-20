package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDate;

public interface ItemBookingDateCommentsView {
    Long getId();
    String getName();
    String getDescription();
    Boolean getAvailable();
    ItemRequest getRequest();
    LocalDate getLastBookingDate();
    LocalDate getNextBookingDate();

    default ItemDto getItemDto() {
        return new ItemDto(getId(), getName(), getDescription(), getAvailable(), /*todo:*/null);
    }
}
