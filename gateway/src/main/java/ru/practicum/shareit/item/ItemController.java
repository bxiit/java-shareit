package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "Плохой идентификатор") long userId,
            @RequestBody @Valid ItemDto itemDto
    ) {
        return itemClient.addItem(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId
    ) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByQuery(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "text", required = false, defaultValue = "") String text
    ) {
        return itemClient.getItems(userId, text);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody @Valid UpdateItemRequest request
    ) {
        return itemClient.updateItem(userId, itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(name = "itemId") long itemId
    ) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(name = "itemId") long itemId,
            @RequestBody NewCommentRequest request
    ) {
        return itemClient.addComment(userId, itemId, request);
    }
}

