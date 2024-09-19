package ru.practicum.shareit.item.mappers;

import jakarta.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        uses = {UserMapper.class, ItemRequestMapper.class, BookingMapper.class, CommentMapper.class}
)
public interface ItemMapper {

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto mapToDto(Item item, @Nullable Booking lastBooking, @Nullable Booking nextBooking, List<Comment> comments);

    default ItemDto mapToDto(Item item) {
        return mapToDto(item, null, null, null);
    }

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "request", source = "itemRequest")
    @Mapping(target = "description", source = "itemDto.description")
    @Mapping(target = "available", source = "itemDto.available")
    Item mapToEntity(ItemDto itemDto, User user, ItemRequest itemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", source = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "available", source = "available", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item updateItemFields(@MappingTarget Item item, UpdateItemRequest request);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "request", source = "item.request")
    ItemInfoDto mapToItemInfoDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments);

    default LocalDateTime map(Instant value) {
        return InstantConverter.toLocalDateTime(value);
    }

    default Instant map(LocalDateTime value) {
        return InstantConverter.toInstant(value);
    }
}
