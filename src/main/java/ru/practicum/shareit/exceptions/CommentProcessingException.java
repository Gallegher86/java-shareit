package ru.practicum.shareit.exceptions;

public class CommentProcessingException extends RuntimeException {
    public CommentProcessingException(String message) {
        super(message);
    }
}
