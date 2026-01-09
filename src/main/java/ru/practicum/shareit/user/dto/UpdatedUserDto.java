package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
public class UpdatedUserDto {
    private String name;
    @Email(message = "Электронный адрес пользователя должен содержать символ @ и быть корректным.")
    private String email;
}
