package ru.practicum.shareit.exceptions;

public class BookingProcessingException extends RuntimeException {
    public BookingProcessingException(String message) {
        super(message);
    }
}
