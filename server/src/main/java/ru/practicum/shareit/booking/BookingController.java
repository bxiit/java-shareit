package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody NewBookingRequest request
    ) {
        return bookingService.save(userId, request);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId
    ) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getByState(
            @RequestHeader("X-Sharer-User-Id") String userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) State state
    ) {
        return bookingService.getByState(Long.valueOf(userId), state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) State state
    ) {
        return bookingService.getByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto edit(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") Boolean approved
    ) {
        return bookingService.update(userId, bookingId, approved);
    }
}
