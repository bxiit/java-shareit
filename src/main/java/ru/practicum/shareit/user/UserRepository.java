package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

interface UserRepository {
    List<User> findAll();

    User save(User user);

    Optional<User> findById(String userId);

    User update(User user);

    void deleteById(String userId);

    Optional<User> findByEmail(String email);
}