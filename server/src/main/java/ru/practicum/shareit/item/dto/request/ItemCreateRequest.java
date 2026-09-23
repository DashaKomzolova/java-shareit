package ru.practicum.shareit.item.dto.request;

import lombok.Data;

@Data
public class ItemCreateRequest {

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
