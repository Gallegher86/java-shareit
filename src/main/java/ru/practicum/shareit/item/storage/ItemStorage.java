package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Item> findAll();

    Item create(User owner, Item item);

    Item update (Item item);

    Optional<Item> findById(Long id);

    boolean existsById(Long id);
}
