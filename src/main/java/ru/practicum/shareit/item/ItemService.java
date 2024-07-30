package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(String userId, ItemDto item);

    void deleteItem(String userId, long itemId);

    List<ItemDto> getItems(String userId);

    ItemDto editItem(String userId, Long itemId, UpdateItemRequest request);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItemsByFilter(String text);
}
