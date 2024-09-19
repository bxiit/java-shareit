package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.AlreadyExistsException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new AlreadyExistsException("errors.409.users.email");
        }
        User user = userMapper.mapToEntity(userDto);
        user = userRepository.save(user);
        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("errors.404.users"));
    }

    @Override
    public Boolean existById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::mapToDto)
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
        user = userMapper.updateUserFields(user, request);

        userRepository.save(user);

        return userMapper.mapToDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}