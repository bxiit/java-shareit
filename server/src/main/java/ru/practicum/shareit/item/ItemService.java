package ru.practicum.shareit.item;


import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto item);

    ItemInfoDto getItem(Long userId, Long itemId);

    List<ItemInfoDto> getItems(Long userId);

    List<ItemDto> getItemsByFilter(String text);

    ItemDto editItem(Long userId, Long itemId, UpdateItemRequest request);

    void deleteItem(Long userId, long itemId);

    CommentDto addNewComment(Long userId, Long itemId, NewCommentRequest request);

    ;
}
