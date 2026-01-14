package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public Booking create(Long userId, BookingRequestDto dto) {
        Item item = itemService.findById(userId, dto.getItemId());
        User user = userService.findById(userId);

        if (!item.getAvailable()) {
            throw new ItemUnavailableException(String.format(
                    "Вещь с id %d не доступна для бронирования.", item.getId()));
        }

        return bookingRepository.save(BookingMapper.toBooking(dto, user, item));
    }
}
