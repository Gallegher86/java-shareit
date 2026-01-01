package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface UserService {
    User create(User user);

    User update (User user);

    User findById(Long id);

    void delete(Long id);

    void validateUserId(Long id);
}
