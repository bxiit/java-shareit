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
import ru.practicum.shareit.config.MappersConfig;
import ru.practicum.shareit.config.PersistEntity;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.testbuilder.ItemTestBuilder;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

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
    void addNewItem() {
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
}