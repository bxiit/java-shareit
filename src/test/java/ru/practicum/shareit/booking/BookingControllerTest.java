package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/db/sql/booking.sql")
    void edit_shouldReturnUpdatedBooking_whenApprovedIsTrue() throws Exception {
        mockMvc.perform(
                        patch("/bookings/1111")
                                .header("X-Sharer-User-Id", 1111)
                                .param("approved", "true")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1111),
                        jsonPath("$.status").value("APPROVED")
                );
    }
}