package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDate;
import java.util.List;

public record ItemAndBookingDatesAndComments(Long id,
                                             String name,
                                             String description,
                                             Boolean available,
                                             ItemRequestDto request,
                                             LocalDate lastBooking,
                                             LocalDate nextBooking,
                                             List<CommentDto> comments) {
}
