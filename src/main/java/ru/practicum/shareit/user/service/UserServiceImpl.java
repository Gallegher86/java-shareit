package ru.practicum.shareit.user.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyUsedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User newUser) {
        validateEmail(newUser.getEmail(), null);
        User user = userRepository.save(newUser);
        log.info("Пользователь с именем/логином {} с id {} добавлен в список.", user.getName(), user.getId());
        return user;
    }

    @Override
    @Transactional
    public User update(User updatedUser) {
        Long id = updatedUser.getId();
        User user = findById(id);
        boolean changed = false;

        if (updatedUser.getEmail() != null) {
            validateEmail(updatedUser.getEmail(), id);
        }

        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
            user.setName(updatedUser.getName());
            changed = true;
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            user.setEmail(updatedUser.getEmail());
            changed = true;
        }

        if (changed) {
            log.info("Данные пользователя с именем/логином {} с id {} обновлены.",
                    user.getName(), user.getId());
        } else {
            log.info("Получен запрос пользователя с именем/логином {} с id {} на обновление, но обновления отсутствуют.",
                    user.getName(), user.getId());
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id %d не найден.", id)));

        log.info("Пользователь с id {} найден.", id);
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try { userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
        log.info("Пользователь с id {} удален.", id);
    }

    @Override
    public void validateUserId(Long id) {
        if (!userRepository.existsById(id)) {
            String errorMessage = String.format("Пользователь с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }
    }

    private void validateEmail(String email, Long id) {
        boolean exists;

        if (id == null) {
            exists = userRepository.existsByEmail(email);
        } else {
            exists = userRepository.existsByEmailAndIdNot(email, id);
        }

        if (exists) {
            String errorMessage = String.format("Электронный адрес %s уже есть в базе данных", email);
            throw new EmailAlreadyUsedException(errorMessage);
        }
    }
}
