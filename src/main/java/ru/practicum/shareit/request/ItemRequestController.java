package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto add(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemRequestDto request
    ) {
        return itemRequestService.addNewItemRequest(userId, request);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.get(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/requests/{requestId}")
    public ItemRequestDto get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("requestId") long requestId
    ) {
        return itemRequestService.get(userId, requestId);
    }

    // TODO: GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
}
