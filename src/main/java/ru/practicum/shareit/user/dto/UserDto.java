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
    Long id;
    @NotBlank(message = "Имя/логин пользователя должен быть указан.")
    String name;
    @NotBlank(message = "Электронный адрес пользователя не может быть пустым.")
    @Email(message = "Электронный адрес пользователя должен содержать символ @ и быть корректным.")
    String email;
}
