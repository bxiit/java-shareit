package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateItemRequest {

    @NotBlank(message = "Пустое имя")
    private String name;

    @Size(max = 200, message = "Слишком длинное описание")
    private String description;
    private Boolean available;
}
