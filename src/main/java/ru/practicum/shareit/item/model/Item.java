package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import ru.practicum.shareit.request.model.ItemRequest;
import lombok.*;
import ru.practicum.shareit.user.model.User;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "items")
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @Transient
    private ItemRequest request;
}
