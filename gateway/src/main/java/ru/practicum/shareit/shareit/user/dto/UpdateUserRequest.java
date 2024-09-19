package ru.practicum.shareit.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email(message = "{errors.400.users.email}")
    private String email;

    private String name;
}
