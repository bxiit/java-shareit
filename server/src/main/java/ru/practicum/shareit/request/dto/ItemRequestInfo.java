package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponseInfo;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestInfo {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private List<ItemResponseInfo> items;
}
