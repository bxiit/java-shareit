package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findByBookerId(Long bookerId);

    @Query("""
        select bk
        from Booking bk
        join fetch bk.item as it
        where it.id in ?1
        """)
    List<Booking> findByItemsIds(Set<Long> itemsIds);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Instant end, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, Instant start, Sort sort);

    @Query("""
            select bk
            from Booking bk
            where bk.booker.id = ?1
            and bk.item.id = ?2
            """)
    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);
}
