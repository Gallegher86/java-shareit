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
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item create(Long userId, Item newItem) {
        User owner = userService.findById(userId);
        newItem.setOwner(owner);
        Item item = itemRepository.save(newItem);
        log.info("Вещь {} с id {} добавлена в список.", item.getName(), item.getId());
        return item;
    }

    @Override
    @Transactional
    public Item update(Long userId, Item updatedItem) {
        Long id = updatedItem.getId();
        Item item = findById(userId, id);
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
    public Item findById(Long userId, Long id) {
        userService.validateUserId(userId);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id %d не найдена.", id)));

        log.info("Вещь с id {} найдена.", id);
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findOwnersItems(Long userId) {
        userService.validateUserId(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        List<Booking> lastBookings = bookingRepository.findItemsLastBookings(itemIds, now);
        List<Booking> nextBookings = bookingRepository.findItemsNextBookings(itemIds, now);

        Map<Long, Booking> lastBookingMap = lastBookings.stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (existing, ignored) -> existing
                ));

        Map<Long, Booking> nextBookingMap = nextBookings.stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (existing, ignored) -> existing
                ));

        return ItemMapper.toItemsDto(items, lastBookingMap, nextBookingMap);
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
    public Comment createComment(Long userId, Long itemId, CommentRequestDto dto) {
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

    @Override
    public List<Comment> getItemComments (Long itemId) {
        validateItemId(itemId);
        return commentRepository.findByItem_Id(itemId);
    }

    private List<Comment> getUserComment(Long userId) {
        userService.validateUserId(userId);
        List<Long> itemIds = itemRepository.findAllByOwnerId(userId).stream().map(Item::getId).toList();

        return commentRepository.findByItem_IdIn(itemIds);
    }

    private void validateItemId(Long id) {
        if (!itemRepository.existsById(id)) {
            String errorMessage = String.format("Вещь с id %d не найдена.", id);
            throw new NotFoundException(errorMessage);
        }
    }
}
