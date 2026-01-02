package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Long userId, Item item);

    //Item update (Item item);

    Item findById(Long userId, Long id);

    /*List<Item> findOwnersItems(Long id);

    List<Item> findByDescription(String description);*/
}
