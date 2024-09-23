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
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.common.UnavailableItemException;
import ru.practicum.shareit.config.MappersConfig;
import ru.practicum.shareit.config.PersistEntity.BookingPersister;
import ru.practicum.shareit.config.PersistEntity.ItemPersister;
import ru.practicum.shareit.config.PersistEntity.ItemRequestPersister;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.testbuilder.BookingTestBuilder;
import ru.practicum.shareit.testbuilder.ItemTestBuilder;
import ru.practicum.shareit.testbuilder.UserTestBuilder;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.config.PersistEntity.UserPersister;

@DataJpaTest
@Transactional
@Import(MappersConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private static final Instant NOW = Instant.now();
    private static final LocalDateTime NOW_LDT = LocalDateTime.now();
    private final BookingService bookingService;
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
        var sourceUsers = new UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();

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
    void getById_shouldThrowNotFoundException_whenBookingDoesNotExists() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        Long bookingId = 312123L;
        // when
        Executable getBookingById = () -> bookingService.getById(user.getId(), bookingId);

        // then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, getBookingById);
        assertThat(notFoundException.getMessage(), equalTo("errors.404.bookings"));
    }

    @Test
    void getByState_shouldReturnOnePastBooking_whenStateIsPast() {
        // given
        User user = UserTestBuilder.aUser().build();
        em.persist(user);

        Item item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        List<Booking> pastCurrentFutureBookings = getPastCurrentFutureBookings(item, user);
        pastCurrentFutureBookings.forEach(em::persist);

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
        pastCurrentFutureBookings.forEach(em::persist);

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
        pastCurrentFutureBookings.forEach(em::persist);

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
        pastCurrentFutureBookings.forEach(em::persist);

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
        pastCurrentFutureBookings.forEach(em::persist);

        Booking rejectedBooking = BookingTestBuilder.aBooking()
                .withItem(item)
                .withBooker(user)
                .withStatus(Status.REJECTED)
                .withStart(Instant.now().minus(14, DAYS))
                .withEnd(Instant.now().minus(7, DAYS))
                .build();
        em.persist(rejectedBooking);

        // when
        List<BookingDto> rejectedBookings = bookingService.getByState(user.getId(), State.REJECTED);

        // then
        assertThat(rejectedBookings.size(), equalTo(1));
        assertThat(rejectedBookings.getFirst().getId(), equalTo(rejectedBooking.getId()));
        assertThat(rejectedBookings.getFirst().getId(), equalTo(rejectedBooking.getId()));
        assertThat(rejectedBookings.getFirst().getStart().isBefore(LocalDateTime.now()), equalTo(true));
        assertThat(rejectedBookings.getFirst().getEnd().isBefore(LocalDateTime.now()), equalTo(true));
    }

    @Test
    void getByOwner_shouldReturnThreeBookings_whenUserHasThreeAndStatusIsWaiting() {
        // given
        var sourceUsers = new UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var sourceBookings = new BookingPersister(sourceItems, sourceUsers).setEntityManager(em).getPersistedData();
        Long ownerId = sourceUsers.get(4).getId();

        // when
        var bookings = bookingService.getByOwner(ownerId, State.WAITING);

        // then
        assertThat(bookings.size(), equalTo(3));
    }

    @Test
    void getByOwner_shouldThrowNotFoundException_whenUserDoesNotHaveAnyBookings() {
        // given
        var sourceUsers = new UserPersister().setEntityManager(em).getPersistedData();
        var sourceItemRequests = new ItemRequestPersister(sourceUsers).setEntityManager(em).getPersistedData();
        var sourceItems = new ItemPersister(sourceUsers, sourceItemRequests).setEntityManager(em).getPersistedData();
        var sourceBookings = new BookingPersister(sourceItems, sourceUsers).setEntityManager(em).getPersistedData();
        Long ownerId = 12345L;

        // when
        Executable getByOwner = () -> bookingService.getByOwner(ownerId, State.WAITING);

        // then
        var notFoundException = assertThrows(NotFoundException.class, getByOwner);
        assertThat(notFoundException.getMessage(), equalTo("errors.404.bookings"));
    }

    @Test
    void update_shouldReturnUpdatedBooking_whenApprovedIsTrue() {
        // given
        var user = UserTestBuilder.aUser().build();
        em.persist(user);
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        var booker = UserTestBuilder.aUser().build();
        em.persist(booker);
        var booking = BookingTestBuilder.aBooking().withItem(item).withBooker(booker).build();
        em.persist(booking);

        // when
        var updatedBooking = bookingService.update(user.getId(), booking.getId(), Boolean.TRUE);

        // then
        assertThat(updatedBooking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void update_shouldReturnUpdatedBooking_whenApprovedIsFalse() {
        // given
        var user = UserTestBuilder.aUser().build();
        em.persist(user);
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        var booker = UserTestBuilder.aUser().build();
        em.persist(booker);
        var booking = BookingTestBuilder.aBooking().withItem(item).withBooker(booker).build();
        em.persist(booking);

        // when
        var updatedBooking = bookingService.update(user.getId(), booking.getId(), Boolean.FALSE);

        // then
        assertThat(updatedBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void update_shouldThrowBadRequestException_whenOwnerIsWrong() {
        // given
        var user = UserTestBuilder.aUser().build();
        em.persist(user);
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        var booker = UserTestBuilder.aUser().build();
        em.persist(booker);
        var booking = BookingTestBuilder.aBooking().withItem(item).withBooker(booker).build();
        em.persist(booking);

        // when
        Executable updateBooking = () -> bookingService.update(12345L, booking.getId(), Boolean.TRUE);

        // then
        var badRequestException = assertThrows(BadRequestException.class, updateBooking);
        assertThat(badRequestException.getMessage(), equalTo("errors.400.bookings.not_allowed"));
    }

    @Test
    void update_shouldThrowNotFoundException_whenBookingDoesNotExist() {
        // given
        var user = UserTestBuilder.aUser().build();
        em.persist(user);
        var item = ItemTestBuilder.anItem().withOwner(user).build();
        em.persist(item);

        var booker = UserTestBuilder.aUser().build();
        em.persist(booker);
        Long notExistingBookingId = 123321123L;

        // when
        Executable updateBooking = () -> bookingService.update(user.getId(), notExistingBookingId, Boolean.TRUE);

        // then
        var notFoundException = assertThrows(NotFoundException.class, updateBooking);
        assertThat(notFoundException.getMessage(), equalTo("errors.404.bookings"));
    }

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
}