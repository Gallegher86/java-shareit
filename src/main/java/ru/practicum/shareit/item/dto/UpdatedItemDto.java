package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Builder(toBuilder = true)
@Getter
@Setter
public class UpdatedItemDto {
    @Length(max = 255, message = "Имя вещи должно включать не более 255 символов.")
    private String name;
    @Length(max = 1024, message = "Описание вещи должно включать не более 1024 символов.")
    private String description;
    private Boolean available;
}
