package ru.practicum.shareit.booking.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.validation.annotation.ValidBookingDates;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookItemRequestDto> {

    @Override
    public boolean isValid(BookItemRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            return true;
        }

        return dto.getStart().isBefore(dto.getEnd());
    }
}
