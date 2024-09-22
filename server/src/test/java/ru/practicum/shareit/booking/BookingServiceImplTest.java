package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.common.UnavailableItemException;
import ru.practicum.shareit.config.MappersConfig;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.testbuilder.BookingTestBuilder;
import ru.practicum.shareit.testbuilder.ItemRequestTestBuilder;
import ru.practicum.shareit.testbuilder.ItemTestBuilder;
import ru.practicum.shareit.testbuilder.UserTestBuilder;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Transactional
@Import(MappersConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private static final Instant NOW = Instant.now();
    private static final LocalDateTime NOW_LDT = LocalDateTime.now();
    @Autowired
    private BookingService bookingService;
    private final EntityManager em;

    @TestConfiguration
    static class Config {
        @Bean
        BookingService service(
                final BookingRepository bookingRepository,
                final UserRepository userRepository,
                final ItemRepository itemRepository,
                final BookingMapper bookingMapper
        ) {
            return new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingMapper);
        }
    }

    @Test
    void save_shouldReturnAddedBooking_whenEverythingIsOK() {
        // given
        List<User> sourceUsers = makeDefaultUsers();
        for (User sourceUser : sourceUsers) {
            em.persist(sourceUser);
        }

        List<ItemRequest> sourceItemRequests = makeDefaultItemRequests(sourceUsers);
        for (ItemRequest sourceItemRequest : sourceItemRequests) {
            em.persist(sourceItemRequest);
        }

        List<Item> sourceItems = makeDefaultItems(sourceUsers, sourceItemRequests);
        for (Item sourceItem : sourceItems) {
            em.persist(sourceItem);
        }

        Long userId = sourceUsers.getFirst().getId();
        Long itemId = sourceItems.getFirst().getId();
        LocalDateTime start = NOW_LDT.minusDays(2);
        LocalDateTime end = NOW_LDT.plusDays(2);
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);

        // when
        BookingDto savedBooking = bookingService.save(userId, request);

        // then
        assertThat(savedBooking.getId(), notNullValue());
        assertThat(savedBooking.getStart(), equalTo(start));
        assertThat(savedBooking.getEnd(), equalTo(end));
        assertThat(savedBooking.getBooker(), notNullValue());
        assertThat(savedBooking.getBooker().getId(), equalTo(userId));
        assertThat(savedBooking.getItem(), notNullValue());
        assertThat(savedBooking.getItem().getId(), equalTo(itemId));
    }

    @Test
    void save_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // given
        long notExistingUserId = 12345;
        long itemId = 1;

        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now(), LocalDateTime.now().plusDays(10), itemId);

        // when
        Executable addNewBooking = () -> bookingService.save(notExistingUserId, request);

        // then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, addNewBooking);
        assertThat(notFoundException.getHttpStatus(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(notFoundException.getMessage(), equalTo("errors.404.users"));
    }

    @Test
    void save_shouldThrowNotFoundException_whenItemDoesNotExist() {
        // given
        long notExistingItemId = 12345;
        User user = UserTestBuilder.aUser().build();
        em.persist(user);
        Long userId = user.getId();

        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now(), LocalDateTime.now().plusDays(10), notExistingItemId);

        // when
        Executable addNewBooking = () -> bookingService.save(userId, request);

        // then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, addNewBooking);
        assertThat(notFoundException.getHttpStatus(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(notFoundException.getMessage(), equalTo("errors.404.items"));
    }

    @Test
    void save_shouldThrowUnavailableItemException_whenItemIsNotAvailable() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);
        Long userId = user.getId();

        Item item = ItemTestBuilder.anItem().withOwner(user).withAvailable(Boolean.FALSE).build();
        em.persist(item);
        Long itemId = item.getId();

        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now(), LocalDateTime.now().plusDays(10), itemId);

        // when
        Executable addNewBooking = () -> bookingService.save(userId, request);

        // then
        UnavailableItemException unavailableItemException = assertThrows(UnavailableItemException.class, addNewBooking);
        assertThat(unavailableItemException.getHttpStatus(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(unavailableItemException.getMessage(), equalTo("errors.400.bookings.unavailable"));
    }

    @Test
    void getById_shouldThrowNotFoundException_whenItemDoesNotExist() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Long userId = user.getId();

        long notExistingItemId = 12345;
        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now(), LocalDateTime.now().plusDays(10), notExistingItemId);

        // when
        Executable saveBooking = () -> bookingService.save(userId, request);

        // then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, saveBooking);
        assertThat(notFoundException.getHttpStatus(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(notFoundException.getMessage(), equalTo("errors.404.items"));
    }

    @Test
    void getById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Long userId = user.getId() + 1;

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        Long itemId = item.getId();
        NewBookingRequest request = new NewBookingRequest(LocalDateTime.now(), LocalDateTime.now().plusDays(10), itemId);

        // when
        Executable saveBooking = () -> bookingService.save(userId, request);

        // then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, saveBooking);
        assertThat(notFoundException.getHttpStatus(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(notFoundException.getMessage(), equalTo("errors.404.users"));
    }

    @Test
    void getById_shouldReturnBooking_whenBookingExists() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        Booking booking = BookingTestBuilder.aBooking().withItem(item).withBooker(user).build();
        em.persist(booking);

        // when
        BookingDto foundBooking = bookingService.getById(user.getId(), booking.getId());

        // then
        assertThat(foundBooking, notNullValue());
        assertThat(foundBooking.getId(), notNullValue());
        assertThat(foundBooking.getStart(), notNullValue());
        assertThat(foundBooking.getEnd(), notNullValue());

        assertThat(foundBooking.getItem().getId(), notNullValue());

        assertThat(foundBooking.getBooker().getId(), notNullValue());
    }

    @Test
    void getByState_shouldReturnOnePastBooking_whenStateIsPast() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        List<Booking> pastCurrentFutureBookings = getPastCurrentFutureBookings(item, user);
        for (Booking booking : pastCurrentFutureBookings) {
            em.persist(booking);
        }

        Booking pastBooking = pastCurrentFutureBookings.getFirst();

        // when
        List<BookingDto> pastBookings = bookingService.getByState(user.getId(), State.PAST);

        // then
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.getFirst().getId(), equalTo(pastBooking.getId()));
        assertThat(pastBookings.getFirst().getStart().isBefore(LocalDateTime.now()), equalTo(true));
        assertThat(pastBookings.getFirst().getEnd().isBefore(LocalDateTime.now()), equalTo(true));
    }

    @Test
    void getByState_shouldReturnOneCurrentBooking_whenStateIsCurrent() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        List<Booking> pastCurrentFutureBookings = getPastCurrentFutureBookings(item, user);
        for (Booking booking : pastCurrentFutureBookings) {
            em.persist(booking);
        }

        Booking currentBooking = pastCurrentFutureBookings.get(1);

        // when
        List<BookingDto> currentBookings = bookingService.getByState(user.getId(), State.CURRENT);

        // then
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.getFirst().getId(), equalTo(currentBooking.getId()));
        assertThat(currentBookings.getFirst().getStart().isBefore(LocalDateTime.now()), equalTo(true));
        assertThat(currentBookings.getFirst().getEnd().isAfter(LocalDateTime.now()), equalTo(true));
    }

    @Test
    void getByState_shouldReturnOneCurrentBooking_whenStateIsFuture() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        List<Booking> pastCurrentFutureBookings = getPastCurrentFutureBookings(item, user);
        for (Booking booking : pastCurrentFutureBookings) {
            em.persist(booking);
        }

        Booking futureBooking = pastCurrentFutureBookings.getLast();

        // when
        List<BookingDto> futureBookings = bookingService.getByState(user.getId(), State.FUTURE);

        // then
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.getFirst().getId(), equalTo(futureBooking.getId()));
        assertThat(futureBookings.getFirst().getStart().isAfter(LocalDateTime.now()), equalTo(true));
        assertThat(futureBookings.getFirst().getEnd().isAfter(LocalDateTime.now()), equalTo(true));
    }

    @Test
    void getByState_shouldReturnOneCurrentBooking_whenStateIsWaiting() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        List<Booking> pastCurrentFutureBookings = getPastCurrentFutureBookings(item, user);
        for (Booking booking : pastCurrentFutureBookings) {
            em.persist(booking);
        }

        Booking futureBooking = pastCurrentFutureBookings.getLast();

        // when
        List<BookingDto> futureBookings = bookingService.getByState(user.getId(), State.WAITING);

        // then
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.getFirst().getId(), equalTo(futureBooking.getId()));
        assertThat(futureBookings.getFirst().getStart().isAfter(LocalDateTime.now()), equalTo(true));
        assertThat(futureBookings.getFirst().getEnd().isAfter(LocalDateTime.now()), equalTo(true));
    }

    @Test
    void getByState_shouldReturnOneCurrentBooking_whenStateIsRejected() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        List<Booking> pastCurrentFutureBookings = getPastCurrentFutureBookings(item, user);
        for (Booking booking : pastCurrentFutureBookings) {
            em.persist(booking);
        }

        Booking rejectedBooking = BookingTestBuilder.aBooking()
                .withItem(item)
                .withBooker(user)
                .withStatus(Status.REJECTED)
                .withStart(Instant.now().minus(14, DAYS))
                .withStart(Instant.now().minus(7, DAYS))
                .build();

        // when

        // then
    }

    //        List<User> sourceUsers = makeDefaultUsers();
    //        for (User sourceUser : sourceUsers) {
    //            em.persist(sourceUser);
    //        }
    //
    //        List<ItemRequest> sourceItemRequests = makeDefaultItemRequests(sourceUsers);
    //        for (ItemRequest sourceItemRequest : sourceItemRequests) {
    //            em.persist(sourceItemRequest);
    //        }
    //
    //        List<Item> sourceItems = makeDefaultItems(sourceUsers, sourceItemRequests);
    //        for (Item sourceItem : sourceItems) {
    //            em.persist(sourceItem);
    //        }
    //
    //        List<Booking> sourceBookings = makeDefaultBooking(sourceUsers, sourceItems);
    //        for (Booking sourceBooking : sourceBookings) {
    //            em.persist(sourceBooking);
    //        }

    private List<Booking> getPastCurrentFutureBookings(Item item, User user) {
        Booking pastBooking = BookingTestBuilder.aBooking()
                .withItem(item)
                .withBooker(user)
                .withStart(NOW.minus(10, DAYS))
                .withEnd(NOW.minus(5, DAYS))
                .withStatus(Status.APPROVED)
                .build();

        Booking currentBooking = BookingTestBuilder.aBooking()
                .withItem(item)
                .withBooker(user)
                .withStart(NOW.minus(2, DAYS))
                .withEnd(NOW.plus(2, DAYS))
                .withStatus(Status.APPROVED)
                .build();

        Booking futureBooking = BookingTestBuilder.aBooking()
                .withItem(item)
                .withBooker(user)
                .withStart(NOW.plus(5, DAYS))
                .withEnd(NOW.plus(10, DAYS))
                .build();
        return List.of(pastBooking, currentBooking, futureBooking);
    }

    private void persistDefaultEntities() {
        List<User> sourceUsers = makeDefaultUsers();
        for (User sourceUser : sourceUsers) {
            em.persist(sourceUser);
        }

        List<ItemRequest> sourceItemRequests = makeDefaultItemRequests(sourceUsers);
        for (ItemRequest sourceItemRequest : sourceItemRequests) {
            em.persist(sourceItemRequest);
        }

        List<Item> sourceItems = makeDefaultItems(sourceUsers, sourceItemRequests);
        for (Item sourceItem : sourceItems) {
            em.persist(sourceItem);
        }

        List<Booking> sourceBookings = makeDefaultBooking(sourceUsers, sourceItems);
        for (Booking sourceBooking : sourceBookings) {
            em.persist(sourceBooking);
        }
    }

    private List<Booking> makeDefaultBooking(List<User> sourceUsers, List<Item> sourceItems) {
        Booking firstBooking = BookingTestBuilder.aBooking()
                .withStart(NOW.minus(2 * 365, DAYS))
                .withEnd(NOW.minus(365, DAYS))
                .withItem(sourceItems.getFirst())
                .withBooker(sourceUsers.get(1))
                .withStatus(Status.APPROVED)
                .build();
        Booking secondBooking = BookingTestBuilder.aBooking()
                .withStart(NOW.minus(7, DAYS))
                .withEnd(NOW.minus(5, DAYS))
                .withItem(sourceItems.getFirst())
                .withBooker(sourceUsers.get(2))
                .withStatus(Status.APPROVED)
                .build();
        Booking thirdBooking = BookingTestBuilder.aBooking()
                .withStart(NOW.plus(5, DAYS))
                .withEnd(NOW.plus(7, DAYS))
                .withItem(sourceItems.getFirst())
                .withBooker(sourceUsers.get(3))
                .build();
        Booking fourthBooking = BookingTestBuilder.aBooking()
                .withStart(NOW.plus(365, DAYS))
                .withEnd(NOW.plus(2 * 365, DAYS))
                .withItem(sourceItems.getFirst())
                .withBooker(sourceUsers.get(4))
                .build();
        Booking fifthBooking = BookingTestBuilder.aBooking()
                .withStart(NOW.plus(30, MINUTES))
                .withEnd(NOW.plus(60, MINUTES))
                .withItem(sourceItems.get(1))
                .withBooker(sourceUsers.get(4))
                .build();
        Booking sixthBooking = BookingTestBuilder.aBooking()
                .withStart(NOW.minus(30, MINUTES))
                .withEnd(NOW.minus(60, MINUTES))
                .withItem(sourceItems.get(1))
                .withBooker(sourceUsers.get(4))
                .build();

        return List.of(firstBooking, secondBooking, thirdBooking, fourthBooking, fifthBooking, sixthBooking);
    }

    private List<Item> makeDefaultItems(List<User> sourceUsers, List<ItemRequest> sourceItemRequests) {
        Item firstItem = ItemTestBuilder.anItem()
                .withName("Item of first user")
                .withDescription("Description of item of first user")
                .withAvailable(true)
                .withOwner(sourceUsers.getFirst())
                .build();

        Item secondItem = ItemTestBuilder.anItem()
                .withName("Item of second user")
                .withDescription("Description of item of second user")
                .withAvailable(true)
                .withOwner(sourceUsers.get(1))
                .build();

        Item thirdItem = ItemTestBuilder.anItem()
                .withName("Item response for 1")
                .withDescription("Response item for request with id 1")
                .withAvailable(true)
                .withOwner(sourceUsers.get(1))
                .build();

        Item fourthItem = ItemTestBuilder.anItem()
                .withName("Item response for 1")
                .withDescription("Second response item for request with id 1")
                .withAvailable(true)
                .withOwner(sourceUsers.get(1))
                .withRequest(sourceItemRequests.getFirst())
                .build();

        Item fifthItem = ItemTestBuilder.anItem()
                .withName("Item response for 2")
                .withDescription("Response item for request with id 2")
                .withAvailable(true)
                .withOwner(sourceUsers.get(1))
                .withRequest(sourceItemRequests.get(1))
                .build();

        return List.of(firstItem, secondItem, thirdItem, fourthItem, fifthItem);
    }

    private List<ItemRequest> makeDefaultItemRequests(List<User> sourceUsers) {
        ItemRequest firstItemRequest = ItemRequestTestBuilder.anItemRequest()
                .withRequestor(sourceUsers.getFirst())
                .withDescription("urgently need some food")
                .build();
        ItemRequest secondItemRequest = ItemRequestTestBuilder.anItemRequest()
                .withRequestor(sourceUsers.getFirst())
                .withDescription("does anyone have headphones?")
                .build();

        return List.of(firstItemRequest, secondItemRequest);
    }

    private List<User> makeDefaultUsers() {
        User defaultUser = UserTestBuilder.aUser().build();
        User secondUser = UserTestBuilder.aUser().withEmail("booker1@gmail.com").withName("booker1").build();
        User thirdUser = UserTestBuilder.aUser().withEmail("booker2@gmail.com").withName("booker2").build();
        User fourthUser = UserTestBuilder.aUser().withEmail("booker3@gmail.com").withName("booker3").build();
        User fifthUser = UserTestBuilder.aUser().withEmail("booker4@gmail.com").withName("booker4").build();
        return List.of(defaultUser, secondUser, thirdUser, fourthUser, fifthUser);
    }
}