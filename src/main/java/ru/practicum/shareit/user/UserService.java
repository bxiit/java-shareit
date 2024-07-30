package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);

    UserDto updateUser(Long userId, UpdateUserRequest request);

    void deleteUserById(Long userId);

    UserDto getUserById(Long userId);
}