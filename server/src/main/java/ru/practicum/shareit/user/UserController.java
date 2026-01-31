package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя с именем/логином {}.", userDto.getName());
        User user = UserMapper.toUserCreated(userDto);
        User savedUser = userService.create(user);
        return UserMapper.toUserDto(savedUser);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UpdatedUserDto updatedUserDto) {
        log.info("Получен запрос на обновление пользователя с id {}.", id);
        User user = UserMapper.toUserUpdated(updatedUserDto);
        user.setId(id);
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
