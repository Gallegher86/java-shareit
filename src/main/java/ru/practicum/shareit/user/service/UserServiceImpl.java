package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyUsedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User create(User newUser) {
        validateEmail(newUser.getEmail());
        User user = userStorage.create(newUser);
        log.info("Пользователь с именем/логином {} с id {} добавлен в список.", user.getName(), user.getId());
        return user;
    }

    @Override
    public User update(User updatedUser) {
        Long id = updatedUser.getId();
        User inMemoryUser = findById(id);

        if (updatedUser.getEmail() != null) {
            validateEmail(updatedUser.getEmail());
        }

        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
            inMemoryUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            inMemoryUser.setEmail(updatedUser.getEmail());
        }

        User user = userStorage.update(inMemoryUser);
        log.info("Обновленный пользователь с именем/логином {} с id {} добавлен в список.",
                user.getName(), user.getId());
        return user;
    }

    @Override
    public User findById(Long id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id %d не найден.", id)));

        log.info("Пользователь с id {} найден.", id);
        return user;
    }

    @Override
    public void delete(Long id) {
        validateUserId(id);
        userStorage.delete(id);
        log.info("Пользователь с id {} удален.", id);
    }

    @Override
    public void validateUserId(Long id) {
        if (!userStorage.existsById(id)) {
            String errorMessage = String.format("Пользователь с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }
    }

    private void validateEmail(String email) {
        boolean exists = userStorage.findAll().stream()
                .map(User::getEmail)
                .anyMatch(email::equals);

        if (exists) {
            String errorMessage = String.format("Электронный адрес %s уже есть в базе данных", email);
            throw new EmailAlreadyUsedException(errorMessage);
        }
    }
}
