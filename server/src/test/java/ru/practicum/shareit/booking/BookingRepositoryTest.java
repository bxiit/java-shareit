package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.ItemRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void findLastBooking_shouldReturnLastClosestAndNextClosestBooking_whenBookingExists() {
        // given
        Set<Long> itemsIds = Set.of(1L);
        List<Booking> bookings = bookingRepository.findByItemsIdsLastBookings(itemsIds);
        bookings.addAll(bookingRepository.findByItemsIdsNextBookings(itemsIds));
        assertThat(bookings)
                .hasSize(2)
                .extracting(Booking::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }
}