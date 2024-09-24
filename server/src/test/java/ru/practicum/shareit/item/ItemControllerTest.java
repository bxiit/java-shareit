package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.config.PersistEntity;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper json;

    @Test
    void add_shouldReturnAddedItem_whenRequestIsValid() throws Exception {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();

        var userId = sourceItems.getFirst().getId();
        var itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("Description of new item");

        // when
        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(itemDto))
                )
                // then
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql"
    })
    void getItemsByQuery_shouldReturnEmptyList_whenQueryDoesMatchesAnyItem() throws Exception {
        // given
        var query = "item";

        // when
        mockMvc.perform(
                        get("/items/search")
                                .param("text", query)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql"
    })
    void getItemsByQuery_shouldReturnEmptyList_whenQueryIsBlank() throws Exception {
        // given
        var query = "";

        // when
        mockMvc.perform(
                        get("/items/search")
                                .param("text", query)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql"
    })
    void editItem_shouldReturnUpdatedItem_whenRequestIsValid() throws Exception {
        // given
        var itemId = 1;
        var userId = 1;
        var updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Item");

        // when
        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .header("X-Sharer-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(updateRequest))
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("Updated Item")
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql"
    })
    void editItem_shouldThrowForbiddenException_whenOwnerIsWrong() throws Exception {
        // given
        var itemId = 1;
        var userId = 123;
        var updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Item");

        // when
        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .header("X-Sharer-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(updateRequest))
                )
                // then
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql"
    })
    void deleteItem_shouldReturnNoContent_whenItemIsDeleted() throws Exception {
        // given
        var itemId = 1;
        var userId = 1;

        // when
        mockMvc.perform(
                        delete("/items/{itemId}", itemId)
                                .header("X-Sharer-User-Id", userId)
                )
                // then
                .andExpect(status().isOk());
    }

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

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void saveComment_shouldReturnSavedComment_whenEverythingIsOK() throws Exception {
        // given
        long itemId = 1;
        long userId = 2;
        var request = new NewCommentRequest("Nice!");

        // when
        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .header("X-Sharer-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.authorName").value("booker1")
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void saveComment_shouldThrowBadRequestException_whenBookingIsUncompleted() throws Exception {
        // given
        long itemId = 1;
        long userId = 4;
        var request = new NewCommentRequest("Nice!");

        // when
        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .header("X-Sharer-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @Sql({
            "/db/sql/users.sql",
            "/db/sql/request.sql",
            "/db/sql/item.sql",
            "/db/sql/booking.sql"
    })
    void saveComment_shouldThrowBadRequestException_whenUserDidNotBookedItem() throws Exception {
        // given
        long itemId = 123123;
        long userId = 4;
        var request = new NewCommentRequest("Nice!");

        // when
        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .header("X-Sharer-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isBadRequest()
                );
    }
}