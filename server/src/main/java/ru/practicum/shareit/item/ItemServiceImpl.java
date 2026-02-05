package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.CommentProcessingException;
import ru.practicum.shareit.exceptions.ItemDontBelongToUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Override
    @Transactional
    public Item create(Long userId, ItemDto dto) {
        User owner = userService.findById(userId);
        Item newItem = ItemMapper.toItem(dto);
        newItem.setOwner(owner);

        if (dto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestService.findById(dto.getRequestId());
            newItem.setRequest(itemRequest);
        }

        Item item = itemRepository.save(newItem);
        log.info("Вещь {} с id {} добавлена в список.", item.getName(), item.getId());
        return item;
    }

    @Override
    @Transactional
    public Item update(Long userId, Item updatedItem) {
        userService.validateUserId(userId);
        Long id = updatedItem.getId();
        Item item = getById(id);
        boolean changed = false;

        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemDontBelongToUserException(
                    String.format("Вещь с id %d не принадлежит пользователю с userId %d.", id, userId));
        }

        if (updatedItem.getName() != null && !updatedItem.getName().isBlank()) {
            item.setName(updatedItem.getName());
            changed = true;
        }

        if (updatedItem.getDescription() != null && !updatedItem.getDescription().isBlank()) {
            item.setDescription(updatedItem.getDescription());
            changed = true;
        }

        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
            changed = true;
        }

        if (changed) {
            log.info("Данные вещи {} с id {} обновлены.", item.getName(), id);
        } else {
            log.info("Получен запрос на обновление вещи с id {}, но обновления отсутствуют.", id);
        }
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto findById(Long userId, Long id) {
        userService.validateUserId(userId);
        ItemDto itemDto = ItemMapper.toItemDto(getById(id));
        List<CommentDto> comments = getItemComments(id);
        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findOwnersItems(Long userId) {
        userService.validateUserId(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();
        List<Booking> lastBookings = bookingRepository.findItemsLastBookings(itemIds, now);
        List<Booking> nextBookings = bookingRepository.findItemsNextBookings(itemIds, now);

        Map<Long, Booking> lastBookingMap = mapBooking(lastBookings);
        Map<Long, Booking> nextBookingMap = mapBooking(nextBookings);

        Map<Long, List<CommentDto>> commentsMap = getUserCommentsMap(itemIds);

        return ItemMapper.toItemsDto(items, lastBookingMap, nextBookingMap, commentsMap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByDescription(Long userId, String description) {
        userService.validateUserId(userId);

        if (description == null || description.isBlank()) {
            return List.of();
        }

        return itemRepository.findByDescription(description);
    }

    @Override
    @Transactional
    public Comment createComment(Long userId, Long itemId, IncomingCommentDto dto) {
        userService.validateUserId(userId);
        validateItemId(itemId);

        LocalDateTime now = LocalDateTime.now();

        Booking booking = bookingRepository
                .findByBookerIdAndItemId(userId, itemId)
                .orElseThrow(() -> new CommentProcessingException(
                        String.format("Пользователь с userId %d не бронировал вещь с itemId %d.", userId, itemId)
                ));

        if (booking.getStatus() != BookingStatus.APPROVED ||
                booking.getEnd().isAfter(now)) {
            throw new CommentProcessingException(
                    "Комментарий можно оставить только после завершённого бронирования.");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(booking.getItem())
                .author(booking.getBooker())
                .created(now)
                .build();

        return commentRepository.save(comment);
    }

    private List<CommentDto> getItemComments(Long itemId) {
        return commentRepository.findByItem_Id(itemId)
                .stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public Item getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id %d не найдена.", id)));

        log.info("Вещь с id {} найдена.", id);
        return item;
    }

    private Map<Long, List<CommentDto>> getUserCommentsMap(List<Long> itemIds) {
        return commentRepository.findByItem_IdIn(itemIds).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));
    }

    private void validateItemId(Long id) {
        if (!itemRepository.existsById(id)) {
            String errorMessage = String.format("Вещь с id %d не найдена.", id);
            throw new NotFoundException(errorMessage);
        }
    }

    private Map<Long, Booking> mapBooking(List<Booking> bookings) {
        return bookings.stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (existing, ignored) -> existing
                ));
    }
}
