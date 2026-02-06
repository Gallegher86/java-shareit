package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRole;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @InjectMocks
    BookingServiceImpl bookingService;

    User user;
    User owner;
    Item item;
    BookingRequestDto dto;
    Booking booking;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        owner = new User();
        owner.setId(2L);

        item = new Item();
        item.setId(10L);
        item.setAvailable(true);
        item.setOwner(owner);

        dto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createShouldSaveBookingWhenDataIsValid() {
        Booking savedBooking = new Booking();
        savedBooking.setId(100L);

        when(itemService.getById(item.getId()))
                .thenReturn(item);
        when(userService.findById(user.getId()))
                .thenReturn(user);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        Booking result = bookingService.create(user.getId(), dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());

        verify(userService).validateUserId(user.getId());
        verify(itemService).getById(item.getId());
        verify(userService).findById(user.getId());
        verify(bookingRepository).save(any(Booking.class));
        verifyNoMoreInteractions(userService, itemService, bookingRepository);
    }

    @Test
    void createShouldThrowExceptionWhenItemIsUnavailable() {
        item.setAvailable(false);

        when(itemService.getById(item.getId()))
                .thenReturn(item);

        assertThrows(ItemUnavailableException.class,
                () -> bookingService.create(user.getId(), dto));

        verify(userService).validateUserId(user.getId());
        verify(itemService).getById(item.getId());
        verify(bookingRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemService, bookingRepository);
    }

    @Test
    void createShouldThrowExceptionWhenUserIsOwner() {
        item.setOwner(user);

        when(itemService.getById(item.getId()))
                .thenReturn(item);

        assertThrows(BookingProcessingException.class,
                () -> bookingService.create(user.getId(), dto));

        verify(userService).validateUserId(user.getId());
        verify(itemService).getById(item.getId());
        verify(bookingRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemService, bookingRepository);
    }

    @Test
    void approveShouldSetStatusApprovedWhenApprovedTrue() {
        when(bookingRepository.findByIdItemOwnerAndStatus(
                1L, 2L, BookingStatus.WAITING))
                .thenReturn(Optional.of(booking));

        Booking result = bookingService.approve(1L, 2L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());

        verify(bookingRepository).findByIdItemOwnerAndStatus(
                1L, 2L, BookingStatus.WAITING);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void approveShouldSetStatusRejectedWhenApprovedFalse() {
        when(bookingRepository.findByIdItemOwnerAndStatus(
                1L, 2L, BookingStatus.WAITING))
                .thenReturn(Optional.of(booking));

        Booking result = bookingService.approve(1L, 2L, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());

        verify(bookingRepository).findByIdItemOwnerAndStatus(
                1L, 2L, BookingStatus.WAITING);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void approveShouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findByIdItemOwnerAndStatus(
                anyLong(), anyLong(), eq(BookingStatus.WAITING)))
                .thenReturn(Optional.empty());

        assertThrows(BookingProcessingException.class,
                () -> bookingService.approve(1L, 2L, true));

        verify(bookingRepository).findByIdItemOwnerAndStatus(
                1L, 2L, BookingStatus.WAITING);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookingShouldReturnBookingWhenUserIsOwnerOrBooker() {
        when(bookingRepository.findByIdItemOwnerOrBooker(1L, 1L))
                .thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(1L, 1L);

        assertNotNull(result);
        assertSame(booking, result);

        verify(bookingRepository)
                .findByIdItemOwnerOrBooker(1L, 1L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookingShouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findByIdItemOwnerOrBooker(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(BookingProcessingException.class,
                () -> bookingService.getBooking(1L, 1L));

        verify(bookingRepository)
                .findByIdItemOwnerOrBooker(1L, 1L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookerBookingsShouldReturnAllBookings() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookingRepository.findAllByItemOwnerOrBooker(
                user.getId(),
                BookingRole.BOOKER.name(),
                pageable))
                .thenReturn(new PageImpl<>(List.of(booking), pageable, 1));

        List<BookingDto> result = bookingService.getBookerBookings(
                user.getId(),
                BookingState.ALL,
                pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService).validateUserId(user.getId());
        verify(bookingRepository).findAllByItemOwnerOrBooker(
                        user.getId(),
                        BookingRole.BOOKER.name(),
                        pageable);
        verifyNoMoreInteractions(userService, bookingRepository);
    }

    @Test
    void getOwnerBookingsShouldReturnAllBookings() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookingRepository.findAllByItemOwnerOrBooker(
                owner.getId(),
                BookingRole.OWNER.name(),
                pageable))
                .thenReturn(new PageImpl<>(List.of(booking), pageable, 1));

        List<BookingDto> result = bookingService.getOwnerBookings(
                owner.getId(),
                BookingState.ALL,
                pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userService).validateUserId(owner.getId());
        verify(bookingRepository)
                .findAllByItemOwnerOrBooker(
                        owner.getId(),
                        BookingRole.OWNER.name(),
                        pageable);
        verifyNoMoreInteractions(userService, bookingRepository);
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getOwnerBookingsShouldCallCorrectRepositoryMethod(BookingState state) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> page = new PageImpl<>(List.of(booking));

        switch (state) {
            case ALL -> when(bookingRepository.findAllByItemOwnerOrBooker(
                    eq(owner.getId()), eq(BookingRole.OWNER.name()), eq(pageable))).thenReturn(page);
            case CURRENT -> when(bookingRepository.findAllStateCurrent(
                    eq(owner.getId()), any(LocalDateTime.class), eq(BookingRole.OWNER.name()), eq(pageable))).thenReturn(page);
            case PAST -> when(bookingRepository.findAllStatePast(
                    eq(owner.getId()), any(LocalDateTime.class), eq(BookingRole.OWNER.name()), eq(pageable))).thenReturn(page);
            case FUTURE -> when(bookingRepository.findAllStateFuture(
                    eq(owner.getId()), any(LocalDateTime.class), eq(BookingRole.OWNER.name()), eq(pageable))).thenReturn(page);
            case WAITING -> when(bookingRepository.findByItemOwnerOrBookerAndStatus(
                    eq(owner.getId()), eq(BookingStatus.WAITING), eq(BookingRole.OWNER.name()), eq(pageable))).thenReturn(page);
            case REJECTED -> when(bookingRepository.findByItemOwnerOrBookerAndStatus(
                    eq(owner.getId()), eq(BookingStatus.REJECTED), eq(BookingRole.OWNER.name()), eq(pageable))).thenReturn(page);
        }

        bookingService.getOwnerBookings(owner.getId(), state, pageable);

        switch (state) {
            case ALL -> verify(bookingRepository).findAllByItemOwnerOrBooker(eq(owner.getId()),
                    eq(BookingRole.OWNER.name()), eq(pageable));
            case CURRENT -> verify(bookingRepository).findAllStateCurrent(eq(owner.getId()),
                    any(LocalDateTime.class), eq(BookingRole.OWNER.name()),eq(pageable));
            case PAST -> verify(bookingRepository).findAllStatePast(eq(owner.getId()),
                    any(LocalDateTime.class), eq(BookingRole.OWNER.name()),eq(pageable));
            case FUTURE -> verify(bookingRepository).findAllStateFuture(eq(owner.getId()),
                    any(LocalDateTime.class), eq(BookingRole.OWNER.name()),eq(pageable));
            case WAITING -> verify(bookingRepository).findByItemOwnerOrBookerAndStatus(eq(owner.getId()),
                    eq(BookingStatus.WAITING), eq(BookingRole.OWNER.name()), eq(pageable));
            case REJECTED -> verify(bookingRepository).findByItemOwnerOrBookerAndStatus(eq(owner.getId()),
                    eq(BookingStatus.REJECTED), eq(BookingRole.OWNER.name()),eq(pageable));
        }

        verify(userService).validateUserId(owner.getId());
        verifyNoMoreInteractions(bookingRepository, userService);
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getBookerBookingsShouldCallCorrectRepositoryMethod(BookingState state) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> page = new PageImpl<>(List.of(booking));

        switch (state) {
            case ALL -> when(bookingRepository.findAllByItemOwnerOrBooker(
                    eq(owner.getId()), eq(BookingRole.BOOKER.name()), eq(pageable))).thenReturn(page);
            case CURRENT -> when(bookingRepository.findAllStateCurrent(
                    eq(owner.getId()), any(LocalDateTime.class), eq(BookingRole.BOOKER.name()), eq(pageable))).thenReturn(page);
            case PAST -> when(bookingRepository.findAllStatePast(
                    eq(owner.getId()), any(LocalDateTime.class), eq(BookingRole.BOOKER.name()), eq(pageable))).thenReturn(page);
            case FUTURE -> when(bookingRepository.findAllStateFuture(
                    eq(owner.getId()), any(LocalDateTime.class), eq(BookingRole.BOOKER.name()), eq(pageable))).thenReturn(page);
            case WAITING -> when(bookingRepository.findByItemOwnerOrBookerAndStatus(
                    eq(owner.getId()), eq(BookingStatus.WAITING), eq(BookingRole.BOOKER.name()), eq(pageable))).thenReturn(page);
            case REJECTED -> when(bookingRepository.findByItemOwnerOrBookerAndStatus(
                    eq(owner.getId()), eq(BookingStatus.REJECTED), eq(BookingRole.BOOKER.name()), eq(pageable))).thenReturn(page);
        }

        bookingService.getBookerBookings(owner.getId(), state, pageable);

        switch (state) {
            case ALL -> verify(bookingRepository).findAllByItemOwnerOrBooker(eq(owner.getId()),
                    eq(BookingRole.BOOKER.name()), eq(pageable));
            case CURRENT -> verify(bookingRepository).findAllStateCurrent(eq(owner.getId()),
                    any(LocalDateTime.class), eq(BookingRole.BOOKER.name()),eq(pageable));
            case PAST -> verify(bookingRepository).findAllStatePast(eq(owner.getId()),
                    any(LocalDateTime.class), eq(BookingRole.BOOKER.name()),eq(pageable));
            case FUTURE -> verify(bookingRepository).findAllStateFuture(eq(owner.getId()),
                    any(LocalDateTime.class), eq(BookingRole.BOOKER.name()),eq(pageable));
            case WAITING -> verify(bookingRepository).findByItemOwnerOrBookerAndStatus(eq(owner.getId()),
                    eq(BookingStatus.WAITING), eq(BookingRole.BOOKER.name()), eq(pageable));
            case REJECTED -> verify(bookingRepository).findByItemOwnerOrBookerAndStatus(eq(owner.getId()),
                    eq(BookingStatus.REJECTED), eq(BookingRole.BOOKER.name()),eq(pageable));
        }

        verify(userService).validateUserId(owner.getId());
        verifyNoMoreInteractions(bookingRepository, userService);
    }
}