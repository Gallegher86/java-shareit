package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя вещи должно быть указано.")
    @Length(max = 255, message = "Имя вещи должно включать не более 255 символов.")
    private String name;
    @NotBlank(message = "У вещи должно быть описание.")
    @Length(max = 1024, message = "Описание вещи должно включать не более 1024 символов.")
    private String description;
    @NotNull(message = "У вещи должна быть указана доступность.")
    private Boolean available;
}
