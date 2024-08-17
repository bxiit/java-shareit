package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

public record ItemWithBookingComments(
        Long id,
        String name,
        String description,
        Boolean available,
        List<CommentDto> comments
) {

}
