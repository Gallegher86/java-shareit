package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ItemDontBelongToUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
}
