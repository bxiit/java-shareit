package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    @Query("""
            select ct
            from Comment ct
            join fetch ct.item it
            where it.id in ?1
            """)
    List<Comment> findByItemsIds(Set<Long> itemsIds);

    List<Comment> findByItemId(Long itemId);
}
