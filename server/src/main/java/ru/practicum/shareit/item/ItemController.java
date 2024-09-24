package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(
            @RequestHeader("X-Sharer-User-Id") String userId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.addNewItem(Long.valueOf(userId), itemDto);
    }

    @GetMapping
    public List<ItemInfoDto> getUserItems(@RequestHeader("X-Sharer-User-Id") String userId) {
        return itemService.getItems(Long.valueOf(userId));
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getItemById(
            @RequestHeader("X-Sharer-User-Id") String userId,
            @PathVariable("itemId") Long itemId
    ) {
        return itemService.getItem(Long.valueOf(userId), itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByQuery(
            @RequestParam(value = "text", required = false, defaultValue = "") String text
    ) {
        return itemService.getItemsByFilter(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(
            @RequestHeader("X-Sharer-User-Id") String userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody UpdateItemRequest request
    ) {
        return itemService.editItem(Long.valueOf(userId), itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") String userId,
            @PathVariable(name = "itemId") Long itemId
    ) {
        itemService.deleteItem(Long.valueOf(userId), itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(
            @RequestHeader("X-Sharer-User-Id") String userId,
            @PathVariable(name = "itemId") Long itemId,
            @RequestBody NewCommentRequest request
    ) {
        return itemService.addNewComment(Long.valueOf(userId), itemId, request);
    }
}