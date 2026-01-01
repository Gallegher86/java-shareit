package ru.practicum.shareit.item.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.request.model.ItemRequest;
import lombok.*;
import ru.practicum.shareit.user.model.User;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Item {
    Long id;
    @NotBlank(message = "Название вещи не должно быть пустым.")
    String name;
    String description;
    @Builder.Default
    ItemStatus available = ItemStatus.AVAILABLE;
    @NotNull
    @Valid
    User owner;
    ItemRequest request;
}
