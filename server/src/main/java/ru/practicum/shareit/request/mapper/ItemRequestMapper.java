package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDateTime;

@Mapper(uses = UserMapper.class)
public interface ItemRequestMapper {

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "requestor", source = "user")
    ItemRequest mapToItemRequest(User user, ItemRequestDto request);

    @Mapping(target = "requestorId", source = "requestor.id")
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);

    default LocalDateTime map(Instant value) {
        return InstantConverter.toLocalDateTime(value);
    }

    default Instant map(LocalDateTime value) {
        return InstantConverter.toInstant(value);
    }
}