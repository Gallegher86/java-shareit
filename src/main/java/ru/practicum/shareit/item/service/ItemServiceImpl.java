package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemDontBelongToUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Item create(Long userId, Item newItem) {
        User owner = userService.findById(userId);
        Item item = itemStorage.create(owner, newItem);
        log.info("Вещь {} с id {} добавлена в список.", item.getName(), item.getId());
        return item;
    }

    @Override
    public Item update(Long userId, Item updatedItem) {
        Long id = updatedItem.getId();
        Item inMemoryItem = findById(userId, id);

        if (!itemStorage.getItemIdsByOwner(userId).contains(id)) {
            throw new ItemDontBelongToUserException(
                    String.format("Вещь с id %d не принадлежит пользователю с userId %d.", id, userId));
        }

        if (updatedItem.getName() != null && !updatedItem.getName().isBlank()) {
            inMemoryItem.setName(updatedItem.getName());
        }

        if (updatedItem.getDescription() != null && !updatedItem.getDescription().isBlank()) {
            inMemoryItem.setDescription(updatedItem.getDescription());
        }

        if (updatedItem.getAvailable() != null) {
            inMemoryItem.setAvailable(updatedItem.getAvailable());
        }

        Item item = itemStorage.update(inMemoryItem);
        log.info("Обновленная вещь {} с id {} добавлена в список.",
                item.getName(), id);
        return item;
    }

    @Override
    public Item findById(Long userId, Long id) {
        userService.validateUserId(userId);

        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id %d не найдена.", id)));

        log.info("Вещь с id {} найдена.", id);
        return item;
    }

    @Override
    public List<Item> findOwnersItems(Long userId) {
        userService.validateUserId(userId);
        return itemStorage.findOwnersItems(userId);
    }

    @Override
    public List<Item> findByDescription(Long userId, String description) {
        userService.validateUserId(userId);

        if (description == null || description.isBlank()) {
            return List.of();
        }

        return itemStorage.findByDescription(description);
    }

    private void validateItemId(Long id) {
        if (!itemStorage.existsById(id)) {
            String errorMessage = String.format("Вещь с id %d не найдена.", id);
            throw new NotFoundException(errorMessage);
        }
    }
}
