package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    @Query("""
            select bk
            from Booking bk
            join fetch bk.item as it
            where it.id in ?1
            """)
    List<Booking> findByItemsIds(Set<Long> itemsIds);

    @Query("""
            select bk
            from Booking bk
            where bk.booker.id = ?1
            and bk.item.id = ?2
            """)
    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("""
            select bk
            from Booking as bk
            where bk.item.id in ?1
            and (cast(bk.start as date) > current_date or cast(bk.start as date) < current_date)
            group by bk.id, bk.item
            having min(bk.start) > current_date or min(bk.start) < current_date
            """)
    List<Booking> findItemsLastNextBookings(Set<Long> itemsIds);

    @Query("""
            select bk
            from (
            select max(l_bk.start) as last_bk_date
            from Booking l_bk
            where l_bk.item.id in ?1
            and cast(l_bk.start as date) < current_date
            group by l_bk.item
            ) as last_booking_date
            inner join Booking bk on bk.start = last_booking_date.last_bk_date
            """)
    List<Booking> lasts(Set<Long> itemsIds);

    @Query("""
            select bk
            from (
            select min(l_bk.start) as last_bk_date
            from Booking l_bk
            where l_bk.item.id in ?1
            and cast(l_bk.start as date) > current_date
            group by l_bk.item
            ) as last_booking_date
            inner join Booking bk on bk.start = last_booking_date.last_bk_date
            """)
    List<Booking> nexts(Set<Long> itemsIds);
}
