package ru.practicum.shareit.testbuilder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@With
@NoArgsConstructor(staticName = "aBooking")
@AllArgsConstructor
public class BookingTestBuilder implements TestBuilder<Booking> {
    private Instant start = Instant.now();
    private Instant end = Instant.now().plus(2, ChronoUnit.DAYS);
    private Item item;
    private User booker;
    private Status status = Status.WAITING;

    @Override
    public Booking build() {
        final var booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}
