package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("От пользователя с userId {} получен запрос на добавление вещи {}.", userId, itemDto.getName());
        Item savedItem = itemService.create(userId, itemDto);
        return ItemMapper.toItemDto(savedItem);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id,
                          @RequestBody ItemDto itemDto) {
        log.info("От пользователя с userId {} получен запрос на обновление вещи с id {}.", userId, id);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(id);
        Item updatedItem = itemService.update(userId, item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info("От пользователя с userId {} получен запрос на получение вещи с id {}.", userId, id);
        return itemService.findById(userId, id);
    }

    @GetMapping
    public List<ItemDto> findOwnersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("От пользователя с userId {} получен запрос на получение списка его вещей.", userId);
        return itemService.findOwnersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByDescription(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        log.info("От пользователя с userId {} получен запрос на получение списка вещей по описанию.", userId);
        return itemService.findByDescription(userId, text).stream().map(ItemMapper::toItemDto).toList();
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                  @RequestBody IncomingCommentDto dto) {
        log.info("От пользователя с userId {} получен комментарий.", userId);
        return CommentMapper.toCommentDto(itemService.createComment(userId, itemId, dto));
    }
}
