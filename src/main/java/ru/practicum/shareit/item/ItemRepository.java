package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(Long userId);

    @Query("""
        select it.id
        from Item it
        where it.owner.id = ?1
        """)
    Set<Long> findItemsIdsByOwnerId(Long ownerId);

    void deleteItemByOwner_IdAndId(Long ownerId, Long id);

    @Query("""
            select i from Item as i
            where (i.name ilike ?1 or
            i.description ilike ?1) and
            i.available = true
            """)
    List<Item> searchItemsByTextFilter(String text);
}