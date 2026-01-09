package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Long> getItemIdsByOwner(Long userId);

    Item create(User owner, Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findOwnersItems(Long userId);

    List<Item> findByDescription(String description);

    boolean existsById(Long id);
}
