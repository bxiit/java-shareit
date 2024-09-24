package ru.practicum.shareit.item.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.config.MappersConfig;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@Import(MappersConfig.class)
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void mapToDto_shouldReturnItemDto_whenItemIsNotNull() {
        // given
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        // when
        ItemDto itemDto = itemMapper.mapToDto(item, null, null, null);

        // then
        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Item Name", itemDto.getName());
        assertEquals("Item Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void mapToDto_shouldReturnNull_whenParamsAreNull() {
        // given / when
        ItemDto itemDto = itemMapper.mapToDto(null, null, null, null);
        // then
        assertNull(itemDto);
    }

    @Test
    void mapToEntity_shouldReturnItem_whenItemDtoIsNotNull() {
        // given
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        User user = new User();
        ItemRequest itemRequest = new ItemRequest();

        // when
        Item item = itemMapper.mapToEntity(itemDto, user, itemRequest);

        // then
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Item Name", item.getName());
        assertEquals("Item Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(user, item.getOwner());
        assertEquals(itemRequest, item.getRequest());
    }


    @Test
    void mapToEntity_shouldReturnNull_whenParamsAreNull() {
        // given / when
        Item item = itemMapper.mapToEntity(null, null, null);
        // then
        assertNull(item);
    }

    @Test
    void updateItemFields_shouldReturnNotUpdateItem_whenRequestIsNull() {
        // given
        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);

        // when
        Item updatedItem = itemMapper.updateItemFields(item, null);

        // then
        assertEquals(item, updatedItem);
    }

    @Test
    void mapToItemInfoDto_shouldReturnItemInfoDto_whenItemIsNotNull() {
        // given
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        List<Comment> comments = List.of(new Comment());

        // when
        ItemInfoDto itemInfoDto = itemMapper.mapToItemInfoDto(item, lastBooking, nextBooking, comments);

        // then
        assertNotNull(itemInfoDto);
        assertEquals(1L, itemInfoDto.getId());
        assertEquals("Item Name", itemInfoDto.getName());
        assertEquals("Item Description", itemInfoDto.getDescription());
        assertTrue(itemInfoDto.getAvailable());
        assertNotNull(itemInfoDto.getLastBooking());
        assertNotNull(itemInfoDto.getNextBooking());
        assertNotNull(itemInfoDto.getComments());
        assertEquals(1, itemInfoDto.getComments().size());
    }

    @Test
    void mapToItemInfoDto_shouldReturnNull_whenParamsAreNull() {
        // given
        // when
        ItemInfoDto itemInfoDto = itemMapper.mapToItemInfoDto(null, null, null, null);

        // then
        assertNull(itemInfoDto);
    }
}