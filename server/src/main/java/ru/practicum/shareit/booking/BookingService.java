package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking create(Long userId, BookingRequestDto dto);

    Booking approve(Long bookingId, Long ownerId, boolean approved);

    Booking getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookerBookings(Long bookerId, BookingState state, Pageable pageable);

    List<BookingDto> getOwnerBookings(Long ownerId, BookingState state, Pageable pageable);
}
