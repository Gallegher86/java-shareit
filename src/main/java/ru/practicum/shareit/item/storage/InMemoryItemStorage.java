package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage{
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> itemsByOwner = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item create(User owner, Item item) {
        long id = generateNextId();
        log.trace("Сгенерирован новый id для вещи {}", id);
        item.setId(id);
        items.put(id, item);
        log.trace("Вещь {} с id {} добавлена в список.", item.getName(), item.getId());

        item.setOwner(owner);
        itemsByOwner.computeIfAbsent(owner.getId(), k -> new HashSet<>())
                .add(id);
        log.trace("Вещь с id {} добавлена в список владельца {}", id, owner.getId());
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.trace("Вещь {} с id {} обновлена.", item.getName(), item.getId());
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = items.get(id);
        return Optional.ofNullable(item);
    }

    @Override
    public boolean existsById(Long id) {
        return items.containsKey(id);
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
