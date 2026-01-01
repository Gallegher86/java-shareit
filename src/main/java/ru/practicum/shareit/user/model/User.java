package ru.practicum.shareit.user.model;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class User {
    Long id;
    String name;
    String email;
}
