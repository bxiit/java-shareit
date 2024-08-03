package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    private String ownerId;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequestDto request;
}
