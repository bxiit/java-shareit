package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final MockMvc mvc;
    private final ObjectMapper json;

    @Test
    @Sql({"/db/sql/users.sql"})
    void add_shouldReturnAddedItemRequest_whenEverythingIsOK() throws Exception {
        // given
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription("need stuff");
        request.setCreated(LocalDateTime.now());
        long requestorId = 1L;
        request.setRequestorId(requestorId);

        // when
        mvc.perform(
                        post("/requests")
                                .content(json.writeValueAsString(request))
                                .header(X_SHARER_USER_ID, requestorId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").exists(),
                        jsonPath("$.description").value("need stuff"),
                        jsonPath("$.requestorId").value(1)
                );
    }

    @Test
    void add_shouldThrowNotFoundException_whenRequestorDoesNotExist() throws Exception {
        // given
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription("need stuff");
        request.setCreated(LocalDateTime.now());
        long requestorId = 123123L;
        request.setRequestorId(requestorId);

        // when
        mvc.perform(
                        post("/requests")
                                .content(json.writeValueAsString(request))
                                .header(X_SHARER_USER_ID, requestorId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql({"/db/sql/users.sql", "/db/sql/request.sql"})
    void get_shouldReturnTwoRequests_whenUserHasTwoRequest() throws Exception {
        // given
        long userId = 1;

        // when
        mvc.perform(
                        get("/requests")
                                .header(X_SHARER_USER_ID, userId)
                )
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$..id").exists()
                );
    }

    @Test
    @Sql({"/db/sql/users.sql", "/db/sql/request.sql", "/db/sql/item.sql"})
    void get_shouldReturnItemRequestsWithResponses_whenResponsesExist() throws Exception {
        // given
        long userId = 1;

        // when
        mvc.perform(
                        get("/requests")
                                .header(X_SHARER_USER_ID, userId)
                )
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(2),
                        jsonPath("$..id").exists()
                );
    }

    @Test
    void getAll() {
    }

    @Test
    void testGet() {
    }
}