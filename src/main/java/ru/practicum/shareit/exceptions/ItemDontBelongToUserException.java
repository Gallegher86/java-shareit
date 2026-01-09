package ru.practicum.shareit.exceptions;

public class ItemDontBelongToUserException extends RuntimeException {
    public ItemDontBelongToUserException(String message) {
        super(message);
    }
}
