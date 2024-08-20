package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "{errors.400.items.name}")
    private String name;

    @Size(max = 200, message = "{errors.400.items.description.too_long}")
    @NotNull(message = "{errors.400.items.description.null}")
    private String description;

    @NotNull(message = "{errors.400.items.available.null}")
    private Boolean available;

    private ItemRequestDto request;
}
