package ru.practicum.shareit.exception;

public class ItemIsNotAvailable extends RuntimeException {
    public ItemIsNotAvailable(String message) {
        super(message);
    }
}
