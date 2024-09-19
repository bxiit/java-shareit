package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(long userId, ItemRequestDto request);

    List<ItemRequestDto> get(long userId);

    List<ItemRequestDto> getAll(long userId);

    ItemRequestDto get(long userId, long requestId);
}
