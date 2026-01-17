package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingRequestDto requestDto) {
        log.info("От пользователя с userId {} получен запрос на бронирование вещи с Id {}.",
                userId, requestDto.getItemId());
        return BookingMapper.toDto(bookingService.create(userId, requestDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestParam boolean approved) {
        log.info("От пользователя с userId {} получен запрос на подтверждение бронирования с Id {}.",
                ownerId, bookingId);
        return BookingMapper.toDto(bookingService.approve(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("От пользователя с userId {} получен запрос на просмотр бронирования с Id {}.",
                userId, bookingId);
        return BookingMapper.toDto(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getBookerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("От пользователя с userId {} получен запрос на получение списка всех его бронирований.",
                userId);
        return bookingService.getBookings(userId, state).stream().map(BookingMapper::toDto).toList();
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("От владельца вещей с userId {} получен запрос на получение списка всех его бронирований.",
                userId);
        return bookingService.getBookings(userId, state).stream().map(BookingMapper::toDto).toList();
    }
}
