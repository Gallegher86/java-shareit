package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {
    Booking create(Long userId, BookingRequestDto dto);
}
