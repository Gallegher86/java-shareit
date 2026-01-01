package ru.practicum.shareit.request.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class ItemRequest {
    private Long id;
    private String description;
    @NotNull
    @Valid
    private User requestor;
    @NotNull
    @PastOrPresent
    private LocalDateTime created;
}
