package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.dto.ItemBookingDateCommentsView;

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

    @Query(value = """
                with last_booking as (select blb.item.id as lb_item_id,
                                         max(cast(blb.start as date)) as last_booking_date
                                  from Booking blb
                                  where cast(blb.start as date) < current_date
                                  group by blb.item.id),
                 next_booking as (select bnb.item.id as nb_item_id,
                                         min(cast(bnb.start as date)) as next_booking_date
                                  from Booking bnb
                                  where cast(bnb.start as date) > current_date
                                  group by bnb.item.id)
            select it,
                   lb.last_booking_date as last,
                   nb.next_booking_date as next
            from Item it
                     join last_booking lb on it.id = lb.lb_item_id
                     join next_booking nb on it.id = nb.nb_item_id
            where it.owner.id = ?1
            """)
    List<ItemBookingDateCommentsView> findByOwnerIdWithLastAndNextBooking(Long ownerId);
}
