package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;
}
