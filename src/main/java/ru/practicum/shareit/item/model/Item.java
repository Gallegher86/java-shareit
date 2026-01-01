package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import ru.practicum.shareit.request.model.ItemRequest;
import lombok.*;

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
    @NotBlank(message = "Статус бронирования вещи должен быть указан.")
    ItemStatus available;
    @NotBlank(message = "Имя владельца должно быть указано.")
    String owner;
    ItemRequest request;
}
