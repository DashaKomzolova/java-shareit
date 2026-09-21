package ru.practicum.shareit.request.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemRequestItemDto> items;
}
