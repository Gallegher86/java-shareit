package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.validation.annotation.ValidBookingDates;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@ValidBookingDates
public class BookingRequestDto {
    @NotNull(message = "Id предмета для бронирования должен быть указан.")
    private Long itemId;

    @NotNull(message = "Начало бронирования должно быть указано.")
    @FutureOrPresent(message = "Начало бронирования не может быть в прошлом.")
    private LocalDateTime start;

    @NotNull(message = "Окончание бронирования должно быть указано.")
    @Future(message = "Окончание бронирования должно быть в будущем.")
    private LocalDateTime end;
}
