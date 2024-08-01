package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateItemRequest {

    private String name;

    @Size(max = 200, message = "{errors.400.items.description.too_long}")
    private String description;
    private Boolean available;
}
