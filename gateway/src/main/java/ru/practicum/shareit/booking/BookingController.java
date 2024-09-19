package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.State;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody NewBookingRequest request
    ) {
        return bookingClient.addBooking(userId, request);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("bookingId") long bookingId
    ) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) State state
    ) {
        return bookingClient.getBookingsByState(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) State state
    ) {
        return bookingClient.getBookingsByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> edit(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("bookingId") long bookingId,
            @RequestParam("approved") Boolean approved
    ) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }
}
