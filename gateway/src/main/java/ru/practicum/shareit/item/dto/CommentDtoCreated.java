package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class CommentDtoCreated {
    @Length(max = 1024, message = "Комментарий должен быть не более 1024 символов.")
    private String text;
}
