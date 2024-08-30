package ru.practicum.shareit.item.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemWithBookingsExtractor implements ResultSetExtractor<List<ItemDto>> {
    @Override
    public List<ItemDto> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, ItemDto> items = new LinkedHashMap<>();
        while (rs.next()) {
            Long itemId = rs.getObject("id", Long.class);
            String itemName = rs.getString("name");
            String itemDescription = rs.getString("description");
            Boolean isItemAvailable = rs.getBoolean("is_available");

            BookingDto lastBookingDto = buildLastBookingDto(rs);

            BookingDto nextBookingDto = buidlNextBookingDto(rs);

            ItemDto itemDto = new ItemDto(itemId, itemName, itemDescription, isItemAvailable, null, lastBookingDto, nextBookingDto, null);
            items.put(itemDto.getId(), itemDto);
        }
        return new ArrayList<>(items.values());
    }

    private BookingDto buidlNextBookingDto(ResultSet rs) throws SQLException {
        Long nextBookingId = rs.getObject("next_booking_id", Long.class);
        if (nextBookingId == null) {
            return null;
        }
        Date nextBookingStart = rs.getDate("next_booking_start");
        Date nextBookingEnd = rs.getDate("next_booking_end");
        String nextBookingStatus = rs.getString("next_booking_status");

        return new BookingDto(
                nextBookingId,
                toLocalDateTime(nextBookingStart),
                toLocalDateTime(nextBookingEnd),
                null,
                null,
                toStatus(nextBookingStatus)
        );
    }

    private Status toStatus(String nextBookingStatus) {
        return Status.valueOf(nextBookingStatus);
    }

    private BookingDto buildLastBookingDto(ResultSet rs) throws SQLException {
        Long lastBookingId = rs.getObject("last_booking_id", Long.class);
        if (lastBookingId == null) {
            return null;
        }
        Date lastBookingStart = rs.getDate("last_booking_start");
        Date lastBookingEnd = rs.getDate("last_booking_end");
        String lastBookingStatus = rs.getString("last_booking_status");

        return new BookingDto(
                lastBookingId,
                toLocalDateTime(lastBookingStart),
                toLocalDateTime(lastBookingEnd),
                null,
                null,
                toStatus(lastBookingStatus)
        );
    }

    public LocalDateTime toLocalDateTime(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
