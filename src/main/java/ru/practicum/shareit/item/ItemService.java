package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(long userId, long itemId);

    List<ItemDto> getItems(long userId);

    ItemDto editItem(Long userId, Long itemId, UpdateItemRequest request);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItems(String text);
}
