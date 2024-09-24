package ru.practicum.shareit.booking;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.common.UnavailableItemException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.Boolean.FALSE;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto save(Long userId, NewBookingRequest request) {
        Booking booking = buildBooking(userId, request);
        booking = bookingRepository.save(booking);
        return bookingMapper.mapToDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(bookingMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("errors.404.bookings"));
    }

    @Override
    public List<BookingDto> getByState(Long userId, State state) {
        Predicate queryParams = QBooking.booking.booker.id.eq(userId).and(state.getQueryParams());
        Iterable<Booking> bookings = bookingRepository.findAll(queryParams);
        return StreamSupport.stream(bookings.spliterator(), false)
                .map(bookingMapper::mapToDto)
                .toList();
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, State state) {
        BooleanExpression queryParams = QBooking.booking.booker.id.eq(userId).and(state.getQueryParams());
        Iterable<Booking> bookingsIt = bookingRepository.findAll(queryParams);
        List<Booking> bookings = StreamSupport.stream(bookingsIt.spliterator(), false).toList();

        // If converted iterable -> stream is empty
        if (bookings.isEmpty()) {
            throw new NotFoundException("errors.404.bookings");
        }
        return bookings.stream()
                .map(bookingMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("errors.404.bookings"));

        Long ownerId = booking.getItem().getOwner().getId();

        if (!(ownerId.equals(userId))) {
            throw new BadRequestException("errors.400.bookings.not_allowed");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        }
        return bookingMapper.mapToDto(booking);
    }

    private Booking buildBooking(Long userId, NewBookingRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("errors.404.users"));
        Item item = itemRepository.findById(request.itemId()).orElseThrow(() -> new NotFoundException("errors.404.items"));
        checkItemAvailability(item);
        return bookingMapper.mapNewRequestToEntity(request, item, user);
    }

    private void checkItemAvailability(Item item) {
        if (FALSE.equals(item.getAvailable())) {
            throw new UnavailableItemException("errors.400.bookings.unavailable");
        }
    }
}
