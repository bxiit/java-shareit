package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class ItemControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void getItemById_shouldReturnItemDtoWithLastAndNextBookingDto_whenLastAndNextExistsAndTheyAreFar() throws Exception {
        // given
        var itemId = 1;
        var userId = 1;

        // when
        mockMvc.perform(
                        get("/items/" + itemId)
                                .header("X-Sharer-User-Id", userId)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.nextBooking.id").value(3),
                        jsonPath("$.lastBooking.id").value(2)
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void getItemById_shouldReturnItemDtoWithoutLastAndNextBookingDto_whenBookingsAreBookedToday() throws Exception {
        // given
        var itemId = 2;
        var userId = 22;

        // when
        mockMvc.perform(
                        get("/items/" + itemId)
                                .header("X-Sharer-User-Id", userId)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(2),
                        jsonPath("$.nextBooking").doesNotExist()
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql",
            "/db/sql/comment.sql"
    })
    void getUserItems_shouldReturnThreeComments_whenCommentsAreExist() throws Exception {
        // given
        var userId = 1;

        // when
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", userId)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$..[0].comments.length()").value(3)
                );
        // $..[0].comments -> поле comments первого элемента из списка
    }
}