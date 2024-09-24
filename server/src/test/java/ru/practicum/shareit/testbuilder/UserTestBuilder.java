package ru.practicum.shareit.testbuilder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.practicum.shareit.user.User;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aUser")
public class UserTestBuilder implements TestBuilder<User> {
    private String name = "Bexeiit";
    private String email = "bexeiitatabek@yandex.kz";

    @Override
    public User build() {
        final var user = new User();
        user.setEmail(email);
        user.setName(name);
        return user;
    }
}
