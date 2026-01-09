package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("От пользователя с userId {} получен запрос на добавление вещи {}.", userId, itemDto.getName());
        Item item = ItemMapper.toItemCreated(itemDto);
        Item savedItem = itemService.create(userId, item);
        return ItemMapper.toItemDto(savedItem);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id,
                          @Valid @RequestBody UpdatedItemDto updatedItemDto) {
        log.info("Получен запрос на обновление пользователя с id {}.", id);
        Item item = ItemMapper.toItemUpdated(updatedItemDto);
        item.setId(id);
        Item updatedItem = itemService.update(userId, item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info("От пользователя с userId {} получен запрос на получение вещи с id {}.", userId, id);
        Item item = itemService.findById(userId, id);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> findOwnersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("От пользователя с userId {} получен запрос на получение списка его вещей.", userId);
        return itemService.findOwnersItems(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    @GetMapping("/search")
    public List<ItemDto> findByDescription(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        log.info("От пользователя с userId {} получен запрос на получение списка вещей по описанию.", userId);
        return itemService.findByDescription(userId, text).stream().map(ItemMapper::toItemDto).toList();
    }
}
