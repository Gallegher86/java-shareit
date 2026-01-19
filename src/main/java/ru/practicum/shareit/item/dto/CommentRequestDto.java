package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class CommentRequestDto {
    @Length(max = 1024, message = "Комментарий должен быть не более 1024 символов.")
    Long userId;
    Long ItemId;
    String text;
}
