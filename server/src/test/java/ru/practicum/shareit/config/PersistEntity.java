package ru.practicum.shareit.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.testbuilder.BookingTestBuilder;
import ru.practicum.shareit.testbuilder.ItemRequestTestBuilder;
import ru.practicum.shareit.testbuilder.ItemTestBuilder;
import ru.practicum.shareit.testbuilder.UserTestBuilder;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;

public class PersistEntity {

    public interface Persister<T> {
        T getPersistedData();

        Persister<T> setEntityManager(EntityManager em);
    }

    abstract static class AbstractPersister<T> implements Persister<T> {
        protected EntityManager entityManager;

        @Override
        public Persister<T> setEntityManager(EntityManager em) {
            this.entityManager = em;
            return this;
        }

        void persist(Object entities) {
            if (entities instanceof Collection<?> objects) {
                objects.forEach(entityManager::persist);
            } else {
                entityManager.persist(entities);
            }
        }
    }

    public static class UserPersister extends AbstractPersister<List<User>> {

        @Override
        public List<User> getPersistedData() {
            User defaultUser = UserTestBuilder.aUser().build();
            User secondUser = UserTestBuilder.aUser().withEmail("booker1@gmail.com").withName("booker1").build();
            User thirdUser = UserTestBuilder.aUser().withEmail("booker2@gmail.com").withName("booker2").build();
            User fourthUser = UserTestBuilder.aUser().withEmail("booker3@gmail.com").withName("booker3").build();
            User fifthUser = UserTestBuilder.aUser().withEmail("booker4@gmail.com").withName("booker4").build();
            var users = List.of(defaultUser, secondUser, thirdUser, fourthUser, fifthUser);
            persist(users);
            return users;
        }
    }

    @RequiredArgsConstructor
    public static class ItemRequestPersister extends AbstractPersister<List<ItemRequest>> {

        private final List<User> sourceUsers;

        @Override
        public List<ItemRequest> getPersistedData() {
            ItemRequest firstItemRequest = ItemRequestTestBuilder.anItemRequest()
                    .withRequestor(sourceUsers.getFirst())
                    .withDescription("urgently need some food")
                    .build();
            ItemRequest secondItemRequest = ItemRequestTestBuilder.anItemRequest()
                    .withRequestor(sourceUsers.getFirst())
                    .withDescription("does anyone have headphones?")
                    .build();
            var itemRequests = List.of(firstItemRequest, secondItemRequest);
            persist(itemRequests);
            return itemRequests;
        }
    }

    @RequiredArgsConstructor
    public static class ItemPersister extends AbstractPersister<List<Item>> {

        private final List<User> sourceUsers;
        private final List<ItemRequest> sourceItemRequests;

        @Override
        public List<Item> getPersistedData() {
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

            var items = List.of(firstItem, secondItem, thirdItem, fourthItem, fifthItem);
            persist(items);
            return items;
        }
    }

    @RequiredArgsConstructor
    public static class BookingPersister extends AbstractPersister<List<Booking>> {

        private static final Instant NOW = Instant.now();
        private final List<Item> sourceItems;
        private final List<User> sourceUsers;

        @Override
        public List<Booking> getPersistedData() {
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

            var bookings = List.of(firstBooking, secondBooking, thirdBooking, fourthBooking, fifthBooking, sixthBooking);
            persist(bookings);
            return bookings;
        }
    }
}
