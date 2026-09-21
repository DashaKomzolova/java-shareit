package ru.practicum.shareit.exception;

public class ItemIsABookerItemException extends RuntimeException {
    public ItemIsABookerItemException(String message) {
        super(message);
    }
}
