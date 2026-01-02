package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя вещи должно быть указано.")
    private String name;
    @NotBlank(message = "У вещи должно быть описание.")
    private String description;
    @NotNull(message = "У вещи должна быть указана доступность.")
    private Boolean available;
}
