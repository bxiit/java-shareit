package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

interface UserRepository {
    List<User> findAll();

    User save(User user);

    Optional<User> findById(Long userId);

    User update(User user);

    void deleteById(Long userId);

    Optional<User> findByEmail(String email);
}