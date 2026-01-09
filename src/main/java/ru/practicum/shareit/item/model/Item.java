package ru.practicum.shareit.item.model;

import ru.practicum.shareit.request.model.ItemRequest;
import lombok.*;
import ru.practicum.shareit.user.model.User;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
