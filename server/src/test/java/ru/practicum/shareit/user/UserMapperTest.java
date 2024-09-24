package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.config.MappersConfig;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@Import(MappersConfig.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void mapToDto_shouldReturnNull_whenUserIsNull() {
        // given
        // when
        UserDto userDto = userMapper.mapToDto(null);

        // then
        assertNull(userDto);
    }

    @Test
    void mapToEntity_shouldReturnNull_whenUserDtoIsNull() {
        // given / when
        User user = userMapper.mapToEntity(null);

        // then
        assertNull(user);
    }
}