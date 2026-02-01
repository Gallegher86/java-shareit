package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoUpdated;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemDtoCreated dto) {
        log.info("От пользователя с userId {} получен запрос на добавление вещи {}.", userId, dto.getName());
        return itemClient.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id,
                                         @Valid @RequestBody ItemDtoUpdated dto) {
        log.info("От пользователя с userId {} получен запрос на обновление вещи с id {}.", userId, id);
        return itemClient.update(userId, id, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info("От пользователя с userId {} получен запрос на получение вещи с id {}.", userId, id);
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findOwnersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("От пользователя с userId {} получен запрос на получение списка его вещей.", userId);
        return itemClient.findOwnersItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Size(max = 255, message = "Текст поиска слишком длинный.")
                                                    @RequestParam String text) {
        log.info("От пользователя с userId {} получен запрос на получение списка вещей по описанию.", userId);
        return itemClient.findByDescription(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                              @Valid @RequestBody CommentDtoCreated dto) {
        log.info("От пользователя с userId {} получен комментарий.", userId);
        return itemClient.postComment(userId, itemId, dto);
    }
}
