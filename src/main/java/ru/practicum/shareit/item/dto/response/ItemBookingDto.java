package ru.practicum.shareit.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingDto {
    private Long id;
    private Long bookerId;
}
