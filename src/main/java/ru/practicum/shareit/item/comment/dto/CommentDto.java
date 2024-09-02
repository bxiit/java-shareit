package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

public record CommentDto(Long id,
                         String text,
                         String authorName,
                         ItemDto item,
                         LocalDateTime created) {
}
