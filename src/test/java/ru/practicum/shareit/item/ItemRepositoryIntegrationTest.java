package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemBookingDateCommentsView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ItemRepositoryIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/item.sql",
            "/db/sql/booking_last_next.sql"
    })
    void findByOwnerIdWithLastAndNextBooking() {
        ItemBookingDateCommentsView view = bookingRepository.findByOwnerIdWithLastAndNextBooking(1L).getFirst();
    }
}