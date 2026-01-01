package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User create(User user) {
        long id = generateNextId();
        log.trace("Сгенерирован новый id для пользователя {}", id);
        user.setId(id);
        users.put(id, user);
        log.info("Пользователь с именем/логином {} с id {} добавлен в список.", user.getName(), user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.trace("Пользователь с именем/логином {} с id {} сохранен.", user.getName(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = users.get(id);
        return Optional.ofNullable(user);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        log.trace("Пользователь с id {} удален.", id);
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
