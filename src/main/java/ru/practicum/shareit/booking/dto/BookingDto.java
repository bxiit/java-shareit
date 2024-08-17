package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Setter
@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private Status status;
}
