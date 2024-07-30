package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findByUserId(String userId);

    Item save(Item item);

    void deleteByUserIdAndItemId(String userId, long itemId);

    Optional<Item> findById(Long itemId);

    Optional<Item> findByUserIdAndItemId(String userId, Long itemId);

    Item update(Item item);

    List<Item> findAllByNameLikeIgnoreCase(String text);
}