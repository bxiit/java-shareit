package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
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
    public List<ItemRequestInfo> get(long userId) {
        // own requests and response
        Sort byCreated = Sort.by(ItemRequest.Fields.created);
        List<ItemRequest> userItemRequests = itemRequestRepository.findByRequestorId(userId, byCreated);

        Set<Long> userItemRequestsIds = fetchIds(userItemRequests);

        Map<ItemRequest, List<Item>> itemRequestsResponsesMap = itemRepository.findByRequestIds(userItemRequestsIds).stream()
                .collect(groupingBy(Item::getRequest));

        return userItemRequests.stream()
                .map(request -> itemRequestMapper.mapToItemRequestDto(request, itemRequestsResponsesMap.getOrDefault(request, emptyList())))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        // other requests
        Sort byCreated = Sort.by(ItemRequest.Fields.created);
        return itemRequestRepository.findAll(byCreated).stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestInfo get(long userId, long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findByRequestorIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException("errors.404.requests"));

        Map<ItemRequest, List<Item>> itemRequestResponsesMap = itemRepository.findByRequestIds(fetchIds(itemRequest)).stream()
                .collect(groupingBy(Item::getRequest));

        return itemRequestMapper.mapToItemRequestDto(itemRequest, itemRequestResponsesMap.getOrDefault(itemRequest, emptyList()));
    }

    private Set<Long> fetchIds(ItemRequest itemRequest) {
        return fetchIds(List.of(itemRequest));
    }

    private Set<Long> fetchIds(List<ItemRequest> userItemRequests) {
        return userItemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());
    }
}
