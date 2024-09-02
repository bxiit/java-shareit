package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    String FIND_ITEM_WITH_LAST_AND_NEXT_BOOKINGS_SQL = """
            with
                last_bookings as (
                select lbk.*
                from BOOKING as lbk
                    join (select ITEM_ID,
                                 max(START_DATE) as last_bk_date
                          from BOOKING
                          where cast(START_DATE as date) < current_date
                          group by ITEM_ID) as lbk_max on lbk_max.ITEM_ID = lbk.ITEM_ID
                                                              and lbk_max.last_bk_date = lbk.START_DATE
                ),
                 next_bookings as (
                     select nbk.*
                     from BOOKING as nbk
                     join (select ITEM_ID,
                                  min(START_DATE) as next_bk_date
                           from BOOKING
                           where cast(START_DATE as date) > current_date
                           group by ITEM_ID) as nbk_min on nbk_min.ITEM_ID = nbk.ITEM_ID
                                                               and nbk_min.next_bk_date = nbk.START_DATE
                )
            select it.*,
                   lbks.id as last_booking_id,
                   lbks.start_date as last_booking_start,
                   lbks.end_date as last_booking_end,
                   lbks.item_id as last_booking_item_id,
                   lbks.user_id as last_booking_booker_id,
                   lbks.status as last_booking_status,
                   nbks.ID as next_booking_id,
                   nbks.START_DATE as next_booking_start,
                   nbks.END_DATE as next_booking_end,
                   nbks.ITEM_ID as next_booking_item_id,
                   nbks.USER_ID as next_booking_booker_id,
                   nbks.STATUS as next_booking_status
            from ITEM as it
                     left join last_bookings lbks on lbks.ITEM_ID = it.ID
                     left join next_bookings nbks on nbks.ITEM_ID = it.ID
            where it.OWNER_ID = :ownerId;
            """;

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
}