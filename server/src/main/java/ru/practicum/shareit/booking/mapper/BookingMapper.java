package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDateTime;

@Mapper(uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "status", expression = "java(ru.practicum.shareit.booking.Status.WAITING)")
    @Mapping(target = "start", source = "request.start", qualifiedByName = "mapLocalDateTimeToInstant")
    @Mapping(target = "end", source = "request.end", qualifiedByName = "mapLocalDateTimeToInstant")
    Booking mapNewRequestToEntity(NewBookingRequest request, Item item, User user);

    @Mapping(target = "start", source = "booking.start", qualifiedByName = "mapInstantToLocalDateTime")
    @Mapping(target = "end", source = "booking.end", qualifiedByName = "mapInstantToLocalDateTime")
    BookingDto mapToDto(Booking booking);

    @Named("mapLocalDateTimeToInstant")
    default Instant mapToInstant(LocalDateTime ldt) {
        return InstantConverter.toInstant(ldt);
    }

    @Named("mapInstantToLocalDateTime")
    default LocalDateTime mapToLocalDateTime(Instant instant) {
        return InstantConverter.toLocalDateTime(instant);
    }
}
