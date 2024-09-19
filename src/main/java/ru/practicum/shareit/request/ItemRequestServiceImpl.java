package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public ItemRequestDto addNewItemRequest(long userId, ItemRequestDto request) {
        return null;
    }

    @Override
    public List<ItemRequestDto> get(long userId) {
        return List.of();
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        return List.of();
    }

    @Override
    public ItemRequestDto get(long userId, long requestId) {
        return null;
    }
}
