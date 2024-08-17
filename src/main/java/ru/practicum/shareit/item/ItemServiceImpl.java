package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemAndBookingDatesAndComments;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.filter.BookingDate;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final EntityManager em;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        // Если пользователя с таким id нет, то выбросится 404
        if (!userService.existById(userId)) {
            throw new NotFoundException("errors.404.users");
        }

        User user = em.getReference(User.class, userId);
        Item item = itemMapper.mapToEntity(itemDto, user);
        itemRepository.save(item);

        return itemMapper.mapToDto(item);
    }

    @Override
    public void deleteItem(Long userId, long itemId) {
        itemRepository.deleteItemByOwner_IdAndId(userId, itemId);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, UpdateItemRequest request) {
        // Нахождение вещи которую нужно обновить
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("errors.404.items"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("errors.403.items");
        }
        item = itemMapper.updateItemFields(item, request);
        itemRepository.save(item);
        return itemMapper.mapToDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .map(itemMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("errors.404.items"));
    }

    @Override
    public List<ItemDto> getItemsByFilter(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemsByTextFilter(text).stream()
                .map(itemMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addNewComment(Long userId, Long itemId, NewCommentRequest request) {
        checkForBooking(userId, itemId);
        Comment comment = itemMapper.mapNewRequestToEntity(
                em.getReference(User.class, userId),
                em.getReference(Item.class, itemId),
                request.text()
        );
        commentRepository.save(comment);
        return itemMapper.mapToDto(comment);
    }

    @Override
    public ItemAndBookingDatesAndComments getItemWithBookingComments(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("errors.404.items"));
        Set<Long> itemsIds = getItemsIds(List.of(item));

        Map<Item, List<Booking>> itemBookings = getItemBookingsMap(bookingRepository.findByItemsIds(itemsIds));
        Map<Item, List<Comment>> itemComments = getItemCommentsMap(commentRepository.findAllByItemsIds(itemsIds));

        List<Booking> bookings = itemBookings.getOrDefault(item, Collections.emptyList());
        LocalDate lastBooking = BookingDate.LAST.getBookingDate(bookings);
        LocalDate nextBooking = BookingDate.NEXT.getBookingDate(bookings);

        List<Comment> comments = itemComments.getOrDefault(item, Collections.emptyList());
        return itemMapper.mapToItemBookingDates(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemAndBookingDatesAndComments> getItemsWithBookingComments(Long userId) {
        List<Item> items = itemRepository.findByOwner_Id(userId);
        Set<Long> itemsIds = getItemsIds(items);

        Map<Item, List<Booking>> itemBookings = getItemBookingsMap(bookingRepository.findByItemsIds(itemsIds));
        Map<Item, List<Comment>> itemComments = getItemCommentsMap(commentRepository.findAllByItemsIds(itemsIds));
        return items.stream()
                .map(item -> {
                            List<Booking> bookings = itemBookings.getOrDefault(item, Collections.emptyList()).stream()
                                    .toList();
                            LocalDate lastBooking = BookingDate.LAST.getBookingDate(bookings);
                            LocalDate nextBooking = BookingDate.NEXT.getBookingDate(bookings);

                            List<Comment> comments = itemComments.getOrDefault(item, Collections.emptyList());
                            return itemMapper.mapToItemBookingDates(item, lastBooking, nextBooking, comments);
                        }
                )
                .toList();
    }

    private void checkForBooking(Long userId, Long itemId) {
        // check for booking's booker id, item id and booking has to be past
        Booking booking = bookingRepository.findByBookerIdAndItemId(userId, itemId)
                .orElseThrow(() -> {
                    log.error("user (ID:{}) not allowed to comment item (ID:{})", userId, itemId);
                    return new BadRequestException("errors.400.comments.not_allowed");
                });
        LocalDateTime end = InstantConverter.toLocalDateTime(booking.getEnd());
        if (end.isAfter(LocalDateTime.now())) {
            log.error("user (ID:{}) trying to comment uncompleted booking's item", userId);
            throw new BadRequestException("errors.400.comments.not_allowed");
        }
    }


    private Set<Long> getItemsIds(List<Item> usersItems) {
        return usersItems.stream().map(Item::getId).collect(Collectors.toSet());
    }

    private Map<Item, List<Comment>> getItemCommentsMap(List<Comment> comments) {
        return comments.stream().collect(groupingBy(Comment::getItem));
    }

    private Map<Item, List<Booking>> getItemBookingsMap(List<Booking> bookings) {
        return bookings.stream().collect(groupingBy(Booking::getItem));
    }
}
