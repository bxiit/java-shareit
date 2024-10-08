package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.config.PersistEntity;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final MockMvc mockMvc;
    private final ObjectMapper json;
    private final EntityManager em;

    @Test
    void get_shouldReturnBookingDto_whenBookingExists() throws Exception {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var sourceBookings = new PersistEntity.BookingPersister(sourceItems, sourceUsers).setEntityManager(em).getPersistedData();
        long userId = sourceUsers.getFirst().getId();

        var bookingId = sourceBookings.getFirst().getId();

        // when
        mockMvc.perform(
                        get("/bookings/" + bookingId)
                                .header("X-Sharer-User-Id", userId)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value(Status.APPROVED.name())
                );
    }

    @Test
    void getByState_shouldReturnBookings_whenStateMatches() throws Exception {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var sourceBookings = new PersistEntity.BookingPersister(sourceItems, sourceUsers).setEntityManager(em).getPersistedData();

        long userId = sourceUsers.get(1).getId();
        var state = "ALL";

        // when
        mockMvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", userId)
                                .param("state", state)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").exists()
                );
    }

    @Test
    void getByOwner_shouldReturnBookings_whenOwnerExists() throws Exception {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var sourceBookings = new PersistEntity.BookingPersister(sourceItems, sourceUsers).setEntityManager(em).getPersistedData();

        long userId = sourceUsers.get(1).getId();
        var state = "ALL";

        // when
        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", userId)
                                .param("state", state)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").exists()
                );
    }

    @Test
    void add_shouldReturnAddedBooking_whenEverythingIsOK() throws Exception {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();

        long userId = sourceUsers.getFirst().getId();
        long itemId = sourceItems.getFirst().getId();
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
                        jsonPath("$.start").exists(),
                        jsonPath("$.end").exists(),
                        jsonPath("$.item.id").value(itemId),
                        jsonPath("$.booker.id").value(userId),
                        jsonPath("$.status").value(Status.WAITING.name())
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