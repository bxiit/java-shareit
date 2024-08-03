package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Mapper
public interface ItemMapper {
    ItemMapper MAPPER = Mappers.getMapper(ItemMapper.class);

    ItemDto mapToDto(Item item);

    @Mapping(target = "ownerId", source = "userId")
    Item mapToModel(ItemDto itemDto, String userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", source = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "available", source = "available", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item updateItemFields(@MappingTarget Item item, UpdateItemRequest request);
}
