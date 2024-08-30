package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.extractor.ItemWithBookingsExtractor;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.converter.InstantConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final Pair<Optional<Booking>, Optional<Booking>> EMPTY_PAIR = Pair.of(Optional.empty(), Optional.empty());
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ItemWithBookingsExtractor itemWithBookingsExtractor;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("errors.404.users"));
        Item item = itemMapper.mapToEntity(itemDto, user);
        itemRepository.save(item);

        return itemMapper.mapToDto(item);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("errors.404.items"));
        Set<Long> itemsIds = mapToItemsIds(List.of(item));
        List<Booking> bookings = bookingRepository.lasts(itemsIds);
        bookings.addAll(bookingRepository.nexts(itemsIds));
        Map<Item, Pair<Optional<Booking>, Optional<Booking>>> itemWithBookings = mapToItemPairMap(bookings);
        Pair<Optional<Booking>, Optional<Booking>> lastNextBookings = itemWithBookings.getOrDefault(item, EMPTY_PAIR);
        List<Comment> comments = commentRepository.findByItemId(item.getId());
        return itemMapper.mapToDto(item, lastNextBookings.getFirst().orElse(null), lastNextBookings.getSecond().orElse(null), comments);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        List<ItemDto> itemDtos = jdbcTemplate.query(itemRepository.FIND_ITEM_WITH_LAST_AND_NEXT_BOOKINGS_SQL,
                Map.of("ownerId", userId),
                itemWithBookingsExtractor
        );
        if (itemDtos == null) {
            return null;
        }
        Set<Long> itemsIds = itemDtos.stream().map(ItemDto::getId).collect(toSet());
        Map<ItemDto, List<CommentDto>> itemCommentsMap = commentRepository.findByItemsIds(itemsIds).stream()
                .collect(groupingBy(comment ->
                                        itemMapper.mapToDto(comment.getItem()),
                                mapping(commentMapper::mapToDto, toList())
                        )
                );

        return itemDtos.stream()
                .peek(itemDto -> itemDto.setComments(itemCommentsMap.getOrDefault(itemDto, emptyList())))
                .toList();
    }

    @Override
    public List<ItemDto> getItemsByFilter(String text) {
        if (text.isBlank()) {
            return emptyList();
        }
        return itemRepository.searchItemsByTextFilter(text).stream()
                .map(itemMapper::mapToDto)
                .toList();
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
    public void deleteItem(Long userId, long itemId) {
        itemRepository.deleteItemByOwner_IdAndId(userId, itemId);
    }

    @Override
    @Transactional
    public CommentDto addNewComment(Long userId, Long itemId, NewCommentRequest request) {
        checkForBooking(userId, itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("errors.404.users"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("errors.404.items"));
        Comment comment = commentMapper.mapNewRequestToEntity(
                user,
                item,
                request.text()
        );
        commentRepository.save(comment);
        return commentMapper.mapToDto(comment);
    }

    /**
     * @param bookings - Список бронирований которую нужно преобразовать в мапу,
     *                 где ключ -> вещь, значение -> Пара из последней и следующей брони
     */
    private Map<Item, Pair<Optional<Booking>, Optional<Booking>>> mapToItemPairMap(List<Booking> bookings) {
        return bookings.stream().collect(groupingBy(Booking::getItem))
                .entrySet().stream()
                .collect(toMap(
                                Map.Entry::getKey,
                                entry -> Pair.of(
                                        entry.getValue().stream().filter(booking -> booking.getStart().isBefore(Instant.now())).findFirst(),
                                        entry.getValue().stream().filter(booking -> booking.getStart().isAfter(Instant.now())).findFirst()
                                )
                        )
                );
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
            log.error("user (ID:{}) tried to comment uncompleted booking's item", userId);
            throw new BadRequestException("errors.400.comments.not_allowed");
        }
    }


    private Set<Long> mapToItemsIds(List<Item> usersItems) {
        return usersItems.stream().map(Item::getId).collect(toSet());
    }

    private Map<Item, List<Comment>> getItemCommentsMap(List<Comment> comments) {
        return comments.stream().collect(groupingBy(Comment::getItem));
    }

    private Map<Item, List<Booking>> getItemBookingsMap(List<Booking> bookings) {
        return bookings.stream().collect(groupingBy(Booking::getItem));
    }
}
