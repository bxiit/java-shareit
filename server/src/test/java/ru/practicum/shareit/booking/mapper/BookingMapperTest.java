package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.config.MappersConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@Import(MappersConfig.class)
class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;

    @Test
    void mapToDto_shouldReturnBookingDto_whenBookingIsNotNull() {
        // given
        Booking booking = new Booking();
        booking.setId(1L);

        // when
        BookingDto bookingDto = bookingMapper.mapToDto(booking);

        // then
        assertNotNull(bookingDto);
        assertEquals(1L, bookingDto.getId());
    }

    @Test
    void mapToDto_shouldReturnNull_whenBookingIsNull() {
        // given / when
        BookingDto bookingDto = bookingMapper.mapToDto(null);

        // then
        assertNull(bookingDto);
    }

    @Test
    void mapToEntity_shouldReturnNull_whenBookingDtoIsNull() {
        // given / when
        Booking booking = bookingMapper.mapNewRequestToEntity(null, null, null);

        // then
        assertNull(booking);
    }
}