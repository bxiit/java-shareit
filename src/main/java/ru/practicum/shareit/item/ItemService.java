package ru.practicum.shareit.item;


import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemAndBookingDatesAndComments;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, long itemId);

    ItemDto editItem(Long userId, Long itemId, UpdateItemRequest request);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItemsByFilter(String text);

    CommentDto addNewComment(Long userId, Long itemId, NewCommentRequest request);

    ItemAndBookingDatesAndComments getItemWithBookingComments(Long userId, Long itemId);

    List<ItemAndBookingDatesAndComments> getItemsWithBookingComments(Long userId);
}
