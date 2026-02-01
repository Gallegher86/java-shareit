package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class ItemRequestDtoCreated {
    @Length(max = 1024, message = "Запрос на вещь должен включать не более 1024 символов.")
    private String description;
}
