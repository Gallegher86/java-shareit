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
public class CreatedUserDto {
    private Long id;
    @NotBlank(message = "Имя/логин пользователя должны быть указаны.")
    @Length(max = 255, message = "Имя/логин пользователя должен включать не более 255 символов.")
    private String name;
    @NotBlank(message = "Электронный адрес пользователя не может быть пустым.")
    @Length(max = 512, message = "Электронный адрес пользователя должен включать не более 512 символов.")
    @Email(message = "Электронный адрес пользователя должен содержать символ @ и быть корректным.")
    private String email;
}
