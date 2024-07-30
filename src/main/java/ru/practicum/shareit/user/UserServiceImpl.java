package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new AlreadyExistsException("errors.409.users.email");
        }
        User user = UserMapper.MAPPER.mapToModel(userDto);
        user = userRepository.save(user);
        return UserMapper.MAPPER.mapToDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper.MAPPER::mapToDto)
                .orElseThrow(() -> new NotFoundException("errors.404.users"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper.MAPPER::mapToDto)
                .toList();
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if ((userOptional.isPresent()) && (!userOptional.get().getId().equals(userId))) {
            throw new AlreadyExistsException("errors.409.users.email");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("errors.404.users"));
        user = UserMapper.MAPPER.updateUserFields(user, request);

        user = userRepository.update(user);

        return UserMapper.MAPPER.mapToDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}