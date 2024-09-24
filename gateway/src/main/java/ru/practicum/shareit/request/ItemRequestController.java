package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping()
    public ResponseEntity<Object> add(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemRequestDto request
    ) {
        return itemRequestClient.addItemRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("requestId") long requestId
    ) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
