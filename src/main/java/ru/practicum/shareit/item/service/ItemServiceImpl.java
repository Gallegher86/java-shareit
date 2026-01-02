package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
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
    public Item findById(Long userId, Long id) {
        userService.validateUserId(userId);

        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id %d не найдена.", id)));

        log.info("Вещь с id {} найдена.", id);
        return item;
    }
}
