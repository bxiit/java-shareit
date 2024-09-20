package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto addNewItemRequest(long userId, ItemRequestDto request) {
        log.info("Save item request for userID({})", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("errors.404.users"));
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(user, request);
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> get(long userId) {
        return itemRequestRepository.findByRequestorId(userId).stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        Sort byCreated = Sort.by(ItemRequest.Fields.created);
        return itemRequestRepository.findAll(byCreated).stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto get(long userId, long requestId) {
        return itemRequestRepository.findByRequestorIdAndId(userId, requestId)
                .map(itemRequestMapper::mapToItemRequestDto)
                .orElseThrow(() -> new NotFoundException("errors.404.requests"));
    }
}