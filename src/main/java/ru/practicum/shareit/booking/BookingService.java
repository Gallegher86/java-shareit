package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking create(Long userId, BookingRequestDto dto);

    Booking approve(Long bookingId, Long ownerId, boolean approved);

    Booking getBooking(Long bookingId, Long userId);

    List<Booking> getBookings(Long userId, BookingState state);
}
