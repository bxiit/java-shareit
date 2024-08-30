package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @Email(message = "{errors.400.users.email}")
    @NotBlank(message = "{errors.400.users.email}")
    private String email;

    @NotBlank(message = "{errors.400.users.name}")
    private String name;
}
