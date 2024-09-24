package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    void deleteItemByOwner_IdAndId(Long ownerId, Long id);

    @Query("""
            select i from Item as i
            where (i.name ilike ?1 or
            i.description ilike ?1) and
            i.available = true
            """)
    List<Item> searchItemsByTextFilter(String text);

    @Query("""
            select it
            from Item as it
            join fetch it.owner
            where it.owner.id = ?1
            """)
    List<Item> findByOwnerId(Long ownerId);

    @Query("""
            select it
            from Item it
            join fetch it.request
            where it.request.id in ?1
            """)
    List<Item> findByRequestIds(Set<Long> requestIds);
}