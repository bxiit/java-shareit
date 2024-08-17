package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAndBookingDatesAndComments;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        uses = {UserMapper.class, ItemRequestMapper.class}
)
public interface ItemMapper {
    ItemDto mapToDto(Item item);

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "owner", source = "user")
    Item mapToEntity(ItemDto itemDto, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", source = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "available", source = "available", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item updateItemFields(@MappingTarget Item item, UpdateItemRequest request);

    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "request", source = "item.request")
    ItemAndBookingDatesAndComments mapToItemBookingDates(Item item, LocalDate lastBooking, LocalDate nextBooking, List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Comment mapNewRequestToEntity(User author, Item item, String text);

    @Mapping(target = "authorName", source = "comment.author.name")
    @Mapping(target = "item", source = "comment.item")
    CommentDto mapToDto(Comment comment);

    default LocalDateTime mapToLocalDateTime(Instant instant) {
        return InstantConverter.toLocalDateTime(instant);
    }
}
