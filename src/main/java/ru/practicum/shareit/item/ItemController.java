package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "Плохой идентификатор") String userId,
            @RequestBody @Valid ItemDto itemDto
    ) {
        return itemService.addNewItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") String userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId) {
        return itemService.getItem(itemId);
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
            @RequestBody @Valid UpdateItemRequest request
    ) {
        return itemService.editItem(userId, itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") String userId,
            @PathVariable(name = "itemId") Long itemId
    ) {
        itemService.deleteItem(userId, itemId);
    }
}