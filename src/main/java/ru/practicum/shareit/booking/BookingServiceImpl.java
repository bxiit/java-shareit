package ru.practicum.shareit.booking;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.Boolean.FALSE;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
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
        UserDto userDto = userService.getUserById(userId);
        ItemDto itemDto = itemService.getItem(request.itemId());
        checkItemAvailability(itemDto);
        return bookingMapper.mapNewRequestToEntity(request, itemDto, userDto);
    }

    private void checkItemAvailability(ItemDto itemDto) {
        if (FALSE.equals(itemDto.getAvailable())) {
            throw new UnavailableItemException("errors.400.bookings.unavailable");
        }
    }
}
