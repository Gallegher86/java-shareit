package ru.practicum.shareit.user.model;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;
    private String name;
    private String email;
}
