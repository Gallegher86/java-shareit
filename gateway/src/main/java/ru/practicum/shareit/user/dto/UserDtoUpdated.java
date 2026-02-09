package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Builder(toBuilder = true)
@Getter
@Setter
public class UserDtoUpdated {
    @Length(max = 255, message = "Имя/логин пользователя должен включать не более 255 символов.")
    private String name;
    @Length(max = 512, message = "Электронный адрес пользователя должен включать не более 512 символов.")
    @Email(message = "Электронный адрес пользователя должен содержать символ @ и быть корректным.")
    private String email;
}