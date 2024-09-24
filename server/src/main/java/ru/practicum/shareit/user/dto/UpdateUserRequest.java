package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;

    private String name;
}
