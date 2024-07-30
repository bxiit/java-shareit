package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.util.generator.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        Long maxUsersId = IdGenerator.getMaxUsersId();
        user.setId(maxUsersId);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User update(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }
}