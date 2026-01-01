package ru.practicum.shareit.booking.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Booking {
    Long id;
    @NotNull
    @Future
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;
    @NotNull
    @Valid
    Item item;
    @NotNull
    @Valid
    User booker;
    @Builder.Default
    BookingStatus status = BookingStatus.WAITING;
}
