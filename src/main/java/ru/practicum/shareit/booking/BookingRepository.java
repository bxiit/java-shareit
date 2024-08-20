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

                with last_booking as (select b.ITEM_ID,
                                         max(b.START_DATE) as last_booking_date
                                  from BOOKING b
                                  where b.START_DATE < current_timestamp()
                                    and cast(b.START_DATE as date) <> current_date
                                  group by b.ITEM_ID),
                 next_booking as (select b.ITEM_ID,
                                         min(b.END_DATE) as next_booking_date
                                  from BOOKING b
                                  where b.END_DATE > current_timestamp()
                                    and cast(b.END_DATE as date) <> current_date
                                  group by b.ITEM_ID)
            select i.*,
                   lb.last_booking_date,
                   nb.next_booking_date
            from ITEM i
                     left join last_booking lb on i.ID = lb.ITEM_ID
                     left join next_booking nb on i.ID = nb.ITEM_ID
                     left join PUBLIC.USERS u on i.OWNER_ID = u.ID
            where i.OWNER_ID = ?1
            """, nativeQuery = true)
    List<ItemBookingDateCommentsView> findByOwnerIdWithLastAndNextBooking(Long ownerId);
}
