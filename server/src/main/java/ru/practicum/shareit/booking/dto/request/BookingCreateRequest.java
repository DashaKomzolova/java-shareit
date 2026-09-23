package ru.practicum.shareit.booking.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateRequest {

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;
}
