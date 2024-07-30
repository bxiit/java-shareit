package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.util.generator.IdGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findByUserId(String userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Item save(Item item) {
        Long maxItemsId = IdGenerator.getMaxItemsId();
        item.setId(maxItemsId);
        return items.put(item.getId(), item);
    }

    @Override
    public void deleteByUserIdAndItemId(String userId, long itemId) {
        Iterator<Map.Entry<Long, Item>> entryIterator = items.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Long, Item> entry = entryIterator.next();
            Item item = entry.getValue();
            if (item.getId() == itemId && item.getOwnerId().equals(userId)) {
                entryIterator.remove();
            }
        }
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Optional<Item> findByUserIdAndItemId(String userId, Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public List<Item> findAllByNameLikeIgnoreCase(final String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }
}
