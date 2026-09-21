package ru.practicum.shareit.user.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private Long id;
    private String name;
    private String email;
}
