package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя с именем/логином {}.", userDto.getName());
        User user = UserMapper.toUser(userDto);
        User savedUser = userService.create(user);
        return UserMapper.toUserDto(savedUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto update(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с именем/логином {} с id {}.", userDto.getName(), userDto.getId());
        User user = UserMapper.toUser(userDto);
        User updatedUser = userService.update(user);
        return UserMapper.toUserDto(updatedUser);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с id {}.", id);
        User user = userService.findById(id);
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя с id {}.", id);
        userService.delete(id);
    }
}
