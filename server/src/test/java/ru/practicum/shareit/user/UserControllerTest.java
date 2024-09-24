package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper json;

    @Test
    void saveNewUser_shouldReturnAddedUser_whenEverythingIsOK() throws Exception {
        // given
        UserDto userDto = new UserDto();
        userDto.setName("Bexeiit");
        userDto.setEmail("bexeiitatabek@yandex.kz");

        // when
        mvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(userDto))
                                .accept(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").value("Bexeiit"),
                        jsonPath("$.email").value("bexeiitatabek@yandex.kz")
                );
    }

    @Test
    @Sql({"/db/sql/users.sql"})
    void saveNewUser_shouldThrowAlreadyExistsException_whenEmailIsAlreadyTaken() throws Exception {
        // given
        UserDto userDto = new UserDto();
        userDto.setName("Bexeiit");
        userDto.setEmail("bexeiitatabek@yandex.kz");

        // when
        mvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(userDto))
                                .accept(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    void getUserById_shouldThrowNotFoundException_whenUserDoesNotExist() throws Exception {
        // given
        long notExistingUserId = 1;

        // when
        mvc.perform(
                        get("/users/{id}", notExistingUserId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql({"/db/sql/users.sql"})
    void getUserById_shouldReturnUser_whenUserExists() throws Exception {
        // given
        long existingUserId = 1;

        // when
        mvc.perform(
                        get("/users/{id}", existingUserId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Bexeiit"),
                        jsonPath("$.email").value("bexeiitatabek@yandex.kz")
                );
    }

    @Test
    @Sql({"/db/sql/users.sql"})
    void getAllUsers_shouldReturnUsers_whenThreeUsersExists() throws Exception {
        // given

        // when
        mvc.perform(
                        get("/users")
                                .accept(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(5),
                        jsonPath("$..id").exists()
                );
    }

    @Test
    @Sql({"/db/sql/users.sql"})
    void updateUser_shouldReturnUserWithUpdatedEmail_whenUserExistsAndEmailIsNotTaken() throws Exception {
        // given
        long userId = 1;

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("atabekbekseiit@gmail.com");

        // when
        mvc.perform(
                        patch("/users/{id}", userId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(request))
                )
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Bexeiit"),
                        jsonPath("$.email").value("atabekbekseiit@gmail.com")
                );
    }

    @Test
    @Sql({"/db/sql/users.sql"})
    void updateUser_shouldThrowAlreadyExistsException_whenEmailIsAlreadyTaken() throws Exception {
        // given
        long userId = 1;

        UpdateUserRequest request = new UpdateUserRequest();
        String alreadyTakenEmail = "booker1@gmail.com";
        request.setEmail(alreadyTakenEmail);

        // when
        mvc.perform(
                        patch("/users/{id}", userId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(request))
                )
                // then
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql({"/db/sql/users.sql"})
    void deleteUser_shouldDeleteUser_whenUserExists() throws Exception {
        // given
        long userId = 1;

        // when
        mvc.perform(
                        delete("/users/{id}", userId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpectAll(
                        status().isNoContent()
                );
    }
}