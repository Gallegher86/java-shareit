package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя/логин пользователя должны быть указаны.")
    private String name;
    @NotBlank(message = "Электронный адрес пользователя не может быть пустым.")
    @Email(message = "Электронный адрес пользователя должен содержать символ @ и быть корректным.")
    private String email;
}
