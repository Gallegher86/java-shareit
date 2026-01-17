package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BookingProcessingException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Booking create(Long userId, BookingRequestDto dto) {
        Item item = itemService.findById(userId, dto.getItemId());

        if (!item.getAvailable()) {
            throw new ItemUnavailableException(String.format(
                    "Вещь с id %d не доступна для бронирования.", item.getId()));
        }

        User user = userService.findById(userId);

        return bookingRepository.save(BookingMapper.toBooking(dto, user, item));
    }

    @Override
    @Transactional
    public Booking approve(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findByIdItemOwnerAndStatus(bookingId, ownerId, BookingStatus.WAITING)
                .orElseThrow(() -> new BookingProcessingException("Бронирование с подходящими параметрами не найдено."));

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId, Long userId) {
        return  bookingRepository.findByIdItemOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new BookingProcessingException("Бронирование с подходящими параметрами не найдено."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookings(Long userId, BookingState state) {
        userService.validateUserId(userId);
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerOrBooker(userId);
            case CURRENT -> bookingRepository.findAllStateCurrent(userId, now);
            case PAST -> bookingRepository.findAllStatePast(userId, now);
            case FUTURE -> bookingRepository.findAllStateFuture(userId, now);
            case WAITING -> bookingRepository.findByItemOwnerOrBookerAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerOrBookerAndStatus(userId, BookingStatus.REJECTED);
        };
    }
}
