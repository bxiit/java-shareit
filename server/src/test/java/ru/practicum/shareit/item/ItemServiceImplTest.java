package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.ForbiddenException;
import ru.practicum.shareit.config.MappersConfig;
import ru.practicum.shareit.config.PersistEntity;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.testbuilder.BookingTestBuilder;
import ru.practicum.shareit.testbuilder.ItemTestBuilder;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Transactional
@Import(MappersConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final ItemService itemService;
    private final EntityManager em;
    private final ItemMapper itemMapper;

    @TestConfiguration
    static class Config {
        @Bean
        ItemService itemService(
                ItemRepository itemRepository,
                UserRepository userRepository,
                BookingRepository bookingRepository,
                CommentRepository commentRepository,
                ItemRequestRepository itemRequestRepository,
                ItemMapper itemMapper,
                CommentMapper commentMapper) {
            return new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository, itemMapper, commentMapper);
        }
    }

    @Test
    void addNewItem_shoudAddItem_whenEverythingIsOK() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();

        var user = sourceUsers.getFirst();
        var itemRequest = sourceItemRequests.getFirst();
        var item = ItemTestBuilder.anItem().withOwner(user).withRequest(itemRequest).build();

        // when
        var addedNewItem = itemService.addNewItem(user.getId(), itemMapper.mapToDto(item));

        // then
        assertThat(addedNewItem.getId(), notNullValue());
        assertThat(addedNewItem.getRequestId(), notNullValue());
    }

    @Test
    void addNewItem_shoudAddItem_whenRequestIdIsNull() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();

        var user = sourceUsers.getFirst();
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        // when
        var addedNewItem = itemService.addNewItem(user.getId(), itemMapper.mapToDto(item));

        // then
        assertThat(addedNewItem.getId(), notNullValue());
        assertThat(addedNewItem.getRequestId(), nullValue());
    }

    @Test
    void addNewComment_shouldSaveComment_whenBookingExists() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.get(1);

        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var sourceBookings = new PersistEntity.BookingPersister(sourceItems, sourceUsers).setEntityManager(em).getPersistedData();
        var item = sourceItems.getFirst();

        var request = new NewCommentRequest("Nice!");

        // when
        var savedComment = itemService.addNewComment(user.getId(), item.getId(), request);

        // then
        assertThat(savedComment.id(), notNullValue());
        assertThat(savedComment.text(), equalTo("Nice!"));
    }

    @Test
    void addNewComment_shouldThrowBadRequestException_whenBookingDoesNotExist() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.getFirst();
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        var request = new NewCommentRequest("Nice!");

        // when / then
        assertThrows(BadRequestException.class, () -> itemService.addNewComment(user.getId(), item.getId(), request));
    }

    @Test
    void addNewComment_shouldThrowBadRequestException_whenBookingIsInFuture() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.getFirst();
        var booker = sourceUsers.get(1);

        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        var booking = BookingTestBuilder.aBooking().withItem(item).withBooker(booker).build();
        em.persist(booking);

        var request = new NewCommentRequest("Nice!");

        // when / then
        assertThrows(BadRequestException.class, () -> itemService.addNewComment(booker.getId(), item.getId(), request));
    }

    @Test
    void editItem_shouldUpdateItem_whenUserIsOwner() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.getFirst();
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        var updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Item");
        updateRequest.setDescription("Updated Item Description");
        updateRequest.setAvailable(Boolean.FALSE);

        // when
        var updatedItem = itemService.editItem(user.getId(), item.getId(), updateRequest);

        // then
        assertThat(updatedItem.getName(), equalTo("Updated Item"));
        assertThat(updatedItem.getDescription(), equalTo("Updated Item Description"));
        assertThat(updatedItem.getAvailable(), equalTo(Boolean.FALSE));
    }

    @Test
    void editItem_shouldThrowForbiddenException_whenUserIsNotOwner() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.getFirst();
        var anotherUser = sourceUsers.getLast();
        var item = ItemTestBuilder.anItem().withOwner(anotherUser).build();
        em.persist(item);

        var updateRequest = new UpdateItemRequest();

        // when / then
        assertThrows(ForbiddenException.class, () -> itemService.editItem(user.getId(), item.getId(), updateRequest));
    }

    @Test
    void getItems_shouldReturnItems_whenUserHasItems() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.getFirst();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();

        // when
        List<ItemInfoDto> items = itemService.getItems(user.getId());

        // then
        assertThat(items, is(not(empty())));
        assertThat(items.getFirst().getId(), notNullValue());
    }

    @Test
    void getItemsByFilter_shouldReturnEmptyList_whenTextIsBlank() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var text = "";

        // when
        List<ItemDto> items = itemService.getItemsByFilter(text);

        // then
        assertThat(items.isEmpty(), is(true));
    }

    @Test
    void getItemsByFilter_shouldReturnItems_whenTextIsNotBlank() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new PersistEntity.ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new PersistEntity.ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var text = "Item of first user";

        // when
        List<ItemDto> items = itemService.getItemsByFilter(text);

        // then
        assertThat(items.isEmpty(), is(false));
        assertThat(items.size(), equalTo(1));
    }

    @Test
    void deleteItem_shouldDeleteItem_whenUserIsOwner() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.getFirst();
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        // when
        itemService.deleteItem(user.getId(), item.getId());

        // then
        assertThat(em.find(Item.class, item.getId()), is(nullValue()));
    }

    @Test
    void getItem_shouldReturnItemInfoDto_whenItemExists() {
        // given
        var sourceUsers = new PersistEntity.UserPersister().setEntityManager(em).getPersistedData();
        var user = sourceUsers.getFirst();
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        // when
        ItemInfoDto itemInfo = itemService.getItem(user.getId(), item.getId());

        // then
        assertThat(itemInfo, is(notNullValue()));
        assertThat(itemInfo.getId(), is(item.getId()));
    }
}