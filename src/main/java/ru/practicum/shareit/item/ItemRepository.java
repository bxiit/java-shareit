package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findByUserId(long userId);

    Item save(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    Optional<Item> findById(Long itemId);

    Optional<Item> findByUserIdAndItemId(Long userId, Long itemId);

    Item update(Item item);

    List<Item> findAllByNameLikeIgnoreCase(String text);
}