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
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import java.util.List;

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
    public List<ItemRequestInfo> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        // own requests and response
        return itemRequestService.get(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        // other requests
        return itemRequestService.getAll(userId);
    }

    // user1 -> {"id":1,"email":"Eddie_Kshlerin1@yahoo.com","name":"Rolando Schiller"}
    // user2 -> {"id":2,"email":"Keven35@hotmail.com","name":"Jasmine Schmidt IV"}

    // "x-sharer-user-id":["1"]},"body":{"id":null,"description":"C21M7PhjVp1CRBrdx3Og0gyN1820jXlGzBRzQTZ8PpZebMmvfj","requestorId":null,"created":"2024-09-23T08:35:28.9272332"}
    // "body":{"id":1,"description":"C21M7PhjVp1CRBrdx3Og0gyN1820jXlGzBRzQTZ8PpZebMmvfj","requestorId":1,"created":"2024-09-23T08:35:28.9272332"}

    // "x-sharer-user-id":["2"]},"body":{"id":null,"name":"iEixADlGS5","description":"hxQd79jSNKgHtCKkbFVGgn68xTMHXBTqmpI183jCNve4gFMCZf","available":true,"requestId":1}
    // "body":{"id":1,"name":"iEixADlGS5","description":"hxQd79jSNKgHtCKkbFVGgn68xTMHXBTqmpI183jCNve4gFMCZf","available":true,"requestId":1}

    // {"x-sharer-user-id":["2"], "uri":"http://localhost:9090/requests/1"}
    // "body":{"type":"about:blank","title":"Not Found","status":404,"detail":"Запрос не найден","instance":"/requests/1"}
    @GetMapping("/{requestId}")
    public ItemRequestInfo get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("requestId") long requestId
    ) {
        return itemRequestService.get(userId, requestId);
    }
}
