package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        // Если пользователя с таким id нет, то выбросится 404
        userService.getUserById(userId);

        // Id пользователя сеттится в маппере
        Item item = ItemMapper.MAPPER.mapToModel(itemDto, userId);
        itemRepository.save(item);

        return ItemMapper.MAPPER.mapToDto(item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(ItemMapper.MAPPER::mapToDto)
                .toList();
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, UpdateItemRequest request) {
        // Нахождение вещи которую нужно обновить
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("errors.404.items"));
        if (!item.getOwnerId().equals(userId)) {
            throw new ForbiddenException("errors.403.items");
        }
        item = ItemMapper.MAPPER.updateItemFields(item, request);
        item = itemRepository.update(item);
        return ItemMapper.MAPPER.mapToDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper.MAPPER::mapToDto)
                .orElseThrow(() -> new NotFoundException("errors.404.items"));
    }

    @Override
    public List<ItemDto> getItems(String text) {
        if (!text.isBlank()) {
            return itemRepository.findAllByNameLikeIgnoreCase(text).stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper.MAPPER::mapToDto)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }
}
