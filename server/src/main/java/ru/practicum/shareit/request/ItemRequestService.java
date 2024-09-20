package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(long userId, ItemRequestDto request);

    List<ItemRequestInfo> get(long userId);

    List<ItemRequestDto> getAll(long userId);

    ItemRequestInfo get(long userId, long requestId);
}
