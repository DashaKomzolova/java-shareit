package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.UnknownStateException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        try {
            return BookingState.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException("Unknown state: " + value);
        }
    }
}
