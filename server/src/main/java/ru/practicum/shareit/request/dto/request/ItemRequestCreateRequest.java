package ru.practicum.shareit.request.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateRequest {

    @NotBlank
    private String description;
}
