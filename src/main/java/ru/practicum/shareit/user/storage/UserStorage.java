package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update (User user);

    Optional<User> findById(Long id);

    void delete(Long id);

    boolean existsById(Long id);
}
