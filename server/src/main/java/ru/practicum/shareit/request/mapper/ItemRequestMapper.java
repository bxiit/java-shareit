package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemResponseInfo;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(uses = {UserMapper.class, ItemMapper.class})
public interface ItemRequestMapper {

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "requestor", source = "user")
    @Mapping(target = "created", qualifiedByName = "localDateTimeToInstant")
    ItemRequest mapToItemRequest(User user, ItemRequestDto request);

    @Mapping(target = "requestorId", source = "requestor.id")
    @Mapping(target = "created", qualifiedByName = "instantToLocalDateTime")
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "responses", qualifiedByName = "toResponseInfo", source = "items")
    @Mapping(target = "requestorId", source = "itemRequest.requestor.id")
    @Mapping(target = "created", qualifiedByName = "instantToLocalDateTime")
    ItemRequestInfo mapToItemRequestInfo(ItemRequest itemRequest, List<Item> items);

    @Named("toResponseInfo")
    default List<ItemResponseInfo> toResponseInfo(List<Item> items) {
        return items.stream()
                .map(item -> new ItemResponseInfo(item.getId(), item.getName(), item.getOwner().getId()))
                .toList();
    }

    @Named("instantToLocalDateTime")
    default LocalDateTime map(Instant value) {
        return InstantConverter.toLocalDateTime(value);
    }

    @Named("localDateTimeToInstant")
    default Instant map(LocalDateTime value) {
        return InstantConverter.toInstant(value);
    }
}
