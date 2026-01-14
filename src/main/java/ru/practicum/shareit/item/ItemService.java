package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Long userId, Item item);

    Item update(Long userId, Item item);

    Item findById(Long userId, Long id);

    List<Item> findOwnersItems(Long userId);

    List<Item> findByDescription(Long userId, String description);
}
