package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Comment mapNewRequestToEntity(User author, Item item, String text);

    @Mapping(target = "authorName", source = "comment.author.name")
    @Mapping(target = "item", source = "comment.item")
    @Mapping(target = "item.lastBooking", ignore = true)
    @Mapping(target = "item.nextBooking", ignore = true)
    CommentDto mapToDto(Comment comment);

    default LocalDateTime mapToLocalDateTime(Instant instant) {
        return InstantConverter.toLocalDateTime(instant);
    }
}
