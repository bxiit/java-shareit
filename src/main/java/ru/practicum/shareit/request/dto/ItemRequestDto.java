package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ItemRequestDto {
    private Long id;
}
