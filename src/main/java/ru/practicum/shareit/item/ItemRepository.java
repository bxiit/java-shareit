package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    void deleteItemByOwner_IdAndId(Long ownerId, Long id);

    @Query("""
            select i from Item as i
            where (i.name ilike ?1 or
            i.description ilike ?1) and
            i.available = true
            """)
    List<Item> searchItemsByTextFilter(String text);
}