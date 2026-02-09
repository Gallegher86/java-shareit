package ru.practicum.shareit.booking.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.practicum.shareit.booking.validation.validator.BookingDatesValidator;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingDatesValidator.class)
@Documented
public @interface ValidBookingDates {
    String message() default "Дата начала бронирования не может быть позже даты окончания бронирования.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
