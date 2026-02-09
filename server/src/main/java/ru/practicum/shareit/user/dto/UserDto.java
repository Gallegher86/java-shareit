package ru.practicum.shareit.user.dto;

import lombok.*;

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
