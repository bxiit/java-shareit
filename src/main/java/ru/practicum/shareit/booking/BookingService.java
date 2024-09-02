package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto save(Long userId, NewBookingRequest request);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByState(Long userId, State state);

    List<BookingDto> getByOwner(Long userId, State state);

    BookingDto update(Long userId, Long bookingId, Boolean approved);
}
