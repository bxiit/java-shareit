package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto saveNewUser(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") String id) {
        return userService.getUserById(Long.valueOf(id));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(
            @RequestBody UpdateUserRequest request,
            @PathVariable String id
    ) {
        return userService.updateUser(Long.valueOf(id), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id) {
        userService.deleteUserById(Long.valueOf(id));
    }
}