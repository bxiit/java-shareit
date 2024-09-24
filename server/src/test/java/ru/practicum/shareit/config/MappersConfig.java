package ru.practicum.shareit.config;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mappers.UserMapper;

@TestConfiguration
public class MappersConfig {

    @Bean
    ItemMapper itemMapper() {
        return Mappers.getMapper(ItemMapper.class);
    }

    @Bean
    BookingMapper bookingMapper() {
        return Mappers.getMapper(BookingMapper.class);
    }

    @Bean
    UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    ItemRequestMapper itemRequestMapper() {
        return Mappers.getMapper(ItemRequestMapper.class);
    }

    @Bean
    CommentMapper commentMapper() {
        return Mappers.getMapper(CommentMapper.class);
    }
}
