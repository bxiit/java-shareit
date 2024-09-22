package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerIT {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final MockMvc mockMvc;
    private final ObjectMapper json;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void add_shouldReturnAddedBooking_whenEverythingIsOK() throws Exception {
        // given
        long userId = 1;
        long itemId = 1;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3L);
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);

        // when
        mockMvc.perform(
                        post("/bookings")
                                .header(X_SHARER_USER_ID, userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(request))
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.start").value(start),
                        jsonPath("$.end").value(end),
                        jsonPath("$.item.id").value(itemId),
                        jsonPath("$.booker.id").value(userId),
                        jsonPath("$.status").value(Status.WAITING)
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void edit_shouldReturnUpdatedBooking_whenApprovedIsTrue() throws Exception {
        mockMvc.perform(
                        patch("/bookings/3")
                                .header(X_SHARER_USER_ID, 1)
                                .param("approved", "true")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(3),
                        jsonPath("$.status").value("APPROVED")
                );
    }
}