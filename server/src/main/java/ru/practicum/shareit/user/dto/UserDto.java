package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
