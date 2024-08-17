package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper
public interface ItemRequestMapper {

    ItemRequest mapToEntity(ItemRequestDto itemRequestDto);

    ItemRequestDto mapToDto(ItemRequest itemRequest);
}
