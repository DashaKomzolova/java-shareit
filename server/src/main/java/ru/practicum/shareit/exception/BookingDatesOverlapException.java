package ru.practicum.shareit.exception;

public class BookingDatesOverlapException extends RuntimeException {
    public BookingDatesOverlapException(String message) {
        super(message);
    }
}
