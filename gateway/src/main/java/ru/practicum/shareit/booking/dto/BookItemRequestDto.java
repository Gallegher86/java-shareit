package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validation.annotation.ValidBookingDates;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidBookingDates
public class BookItemRequestDto {
	@NotNull(message = "Id предмета для бронирования должен быть указан.")
	private Long itemId;

	@NotNull(message = "Начало бронирования должно быть указано.")
	@FutureOrPresent(message = "Начало бронирования не может быть в прошлом.")
	private LocalDateTime start;

	@NotNull(message = "Окончание бронирования должно быть указано.")
	@Future(message = "Окончание бронирования должно быть в будущем.")
	private LocalDateTime end;
}
