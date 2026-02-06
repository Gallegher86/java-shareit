package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    User owner;
    User booker;
    User stranger;
    Item item;
    Booking booking;
    Pageable pageable;
    LocalDateTime now;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@mail.com");
        booker = new User(null, "Booker", "booker@mail.com");
        stranger = new User(null, "Stranger", "s@mail.com");
        item = new Item(null, "Drill", "desc", true, owner, null);
        pageable = PageRequest.of(0, 10);
        now = LocalDateTime.now();
        booking = new Booking(null, now.plusDays(1), now.plusDays(2), item, booker, BookingStatus.APPROVED);
    }


    @Test
    void findByIdItemOwnerAndStatusShouldReturnBooking() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Optional<Booking> result =
                bookingRepository.findByIdItemOwnerAndStatus(
                        booking.getId(),
                        owner.getId(),
                        BookingStatus.APPROVED
                );

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem().getName(), result.get().getItem().getName());
    }

    @Test
    void findByIdItemOwnerOrBookerShouldReturnBookingForOwner() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Optional<Booking> result =
                bookingRepository.findByIdItemOwnerOrBooker(
                        booking.getId(),
                        owner.getId()
                );

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem().getName(), result.get().getItem().getName());
    }

    @Test
    void findByIdItemOwnerOrBookerShouldReturnBookingForBooker() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Optional<Booking> result =
                bookingRepository.findByIdItemOwnerOrBooker(
                        booking.getId(),
                        booker.getId()
                );

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem().getName(), result.get().getItem().getName());
    }

    @Test
    void findByIdItemOwnerOrBookerShouldReturnEmptyForOtherUser() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        stranger = userRepository.save(stranger);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Optional<Booking> result =
                bookingRepository.findByIdItemOwnerOrBooker(
                        booking.getId(),
                        stranger.getId()
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemOwnerOrBookerShouldReturnBookingsForBooker() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerOrBooker(
                booker.getId(),
                "BOOKER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllByItemOwnerOrBookerShouldReturnBookingsForOwner() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerOrBooker(
                owner.getId(),
                "OWNER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllByItemOwnerOrBookerShouldReturnEmptyForOtherUser() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        stranger = userRepository.save(stranger);

        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerOrBooker(
                stranger.getId(),
                "OWNER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStateCurrentShouldReturnCurrentBookingForBooker() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        booking = bookingRepository.save(
                new Booking(
                        null,
                        now.minusHours(1),
                        now.plusHours(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateCurrent(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllStateCurrentShouldReturnCurrentBookingForOwner() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        booking = bookingRepository.save(
                new Booking(
                        null,
                        now.minusHours(1),
                        now.plusHours(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateCurrent(
                owner.getId(),
                now,
                "OWNER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllStateCurrentPastBookingShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(3),
                        now.minusDays(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateCurrent(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStateCurrentFutureBookingShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.plusDays(1),
                        now.plusDays(2),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateCurrent(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStateCurrentOtherUserShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        stranger = userRepository.save(stranger);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusHours(1),
                        now.plusHours(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateCurrent(
                stranger.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStatePastShouldReturnPastBookingForBooker() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        booking = bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(3),
                        now.minusDays(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStatePast(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllStatePastShouldReturnPastBookingForOwner() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        booking = bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(5),
                        now.minusDays(2),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStatePast(
                owner.getId(),
                now,
                "OWNER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllStatePastCurrentBookingShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusHours(1),
                        now.plusHours(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStatePast(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStatePastFutureBookingShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.plusDays(1),
                        now.plusDays(2),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStatePast(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStatePastOtherUserShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        stranger = userRepository.save(stranger);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(2),
                        now.minusDays(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStatePast(
                stranger.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStateFutureShouldReturnFutureBookingForBooker() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        booking = bookingRepository.save(
                new Booking(
                        null,
                        now.plusHours(1),
                        now.plusDays(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateFuture(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllStateFutureShouldReturnFutureBookingForOwner() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        booking = bookingRepository.save(
                new Booking(
                        null,
                        now.plusDays(1),
                        now.plusDays(2),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateFuture(
                owner.getId(),
                now,
                "OWNER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findAllStateFutureCurrentBookingShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusHours(1),
                        now.plusHours(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateFuture(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStateFuturePastBookingShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(3),
                        now.minusDays(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateFuture(
                booker.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStateFutureOtherUserShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        stranger = userRepository.save(stranger);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.plusHours(2),
                        now.plusDays(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Page<Booking> result = bookingRepository.findAllStateFuture(
                stranger.getId(),
                now,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemOwnerOrBookerAndStatusShouldReturnBookingForBooker() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findByItemOwnerOrBookerAndStatus(
                booker.getId(),
                BookingStatus.APPROVED,
                "BOOKER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findByItemOwnerOrBookerAndStatusShouldReturnBookingForOwner() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findByItemOwnerOrBookerAndStatus(
                owner.getId(),
                BookingStatus.APPROVED,
                "OWNER",
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(booking.getId(), result.getContent().getFirst().getId());
        assertEquals(booking.getItem().getName(), result.getContent().getFirst().getItem().getName());
    }

    @Test
    void findByItemOwnerOrBookerAndStatusWrongStatusShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findByItemOwnerOrBookerAndStatus(
                booker.getId(),
                BookingStatus.WAITING,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemOwnerOrBookerAndStatusOtherUserShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        stranger = userRepository.save(stranger);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findByItemOwnerOrBookerAndStatus(
                stranger.getId(),
                BookingStatus.APPROVED,
                "BOOKER",
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsLastBookingsShouldReturnLastApprovedPastBookings() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        Item item1 = itemRepository.save(
                new Item(null, "Item1", "desc1", true, owner, null));
        Item item2 = itemRepository.save(
                new Item(null, "Item2", "desc2", true, owner, null));

        Booking older = bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(5),
                        now.minusDays(3),
                        item1,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Booking newer = bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(3),
                        now.minusDays(1),
                        item2,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        List<Booking> result = bookingRepository.findItemsLastBookings(
                List.of(item1.getId(), item2.getId()), now);

        assertEquals(2, result.size());
        assertEquals(newer.getId(), result.get(0).getId());
        assertEquals(older.getId(), result.get(1).getId());
        assertEquals(newer.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(older.getItem().getName(), result.get(1).getItem().getName());
    }

    @Test
    void findItemsLastBookingsFutureBookingShouldBeIgnored() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findItemsLastBookings(
                List.of(item.getId()), now);

        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsLastBookingsNotApprovedShouldBeIgnored() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(2),
                        now.minusDays(1),
                        item,
                        booker,
                        BookingStatus.WAITING
                )
        );

        List<Booking> result = bookingRepository.findItemsLastBookings(
                List.of(item.getId()), now);

        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsLastBookingsItemNotInListShouldBeIgnored() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        Item item1 = itemRepository.save(
                new Item(null, "Item1", "desc1", true, owner, null));
        Item item2 = itemRepository.save(
                new Item(null, "Item2", "desc2", true, owner, null));

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(2),
                        now.minusDays(1),
                        item2,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        List<Booking> result = bookingRepository.findItemsLastBookings(
                List.of(item1.getId()), now);

        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsNextBookingsShouldReturnApprovedFutureBookingsOrderedByEndDesc() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        Item item1 = itemRepository.save(
                new Item(null, "Item1", "desc1", true, owner, null));
        Item item2 = itemRepository.save(
                new Item(null, "Item2", "desc2", true, owner, null));

        Booking earlierEnd = bookingRepository.save(
                new Booking(
                        null,
                        now.plusDays(1),
                        now.plusDays(2),
                        item1,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        Booking laterEnd = bookingRepository.save(
                new Booking(
                        null,
                        now.plusDays(1),
                        now.plusDays(4),
                        item2,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        List<Booking> result = bookingRepository.findItemsNextBookings(
                List.of(item1.getId(), item2.getId()), now);

        assertEquals(2, result.size());
        assertEquals(laterEnd.getId(), result.get(0).getId());
        assertEquals(earlierEnd.getId(), result.get(1).getId());
        assertEquals(laterEnd.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(earlierEnd.getItem().getName(), result.get(1).getItem().getName());
    }

    @Test
    void findItemsNextBookingsPastBookingShouldBeIgnored() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusDays(3),
                        now.minusDays(1),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        List<Booking> result = bookingRepository.findItemsNextBookings(
                List.of(item.getId()), now);

        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsNextBookingsCurrentBookingShouldBeIgnored() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.minusHours(1),
                        now.plusHours(2),
                        item,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        List<Booking> result = bookingRepository.findItemsNextBookings(
                List.of(item.getId()), now);

        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsNextBookingsNotApprovedShouldBeIgnored() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);

        bookingRepository.save(
                new Booking(
                        null,
                        now.plusDays(1),
                        now.plusDays(2),
                        item,
                        booker,
                        BookingStatus.WAITING
                )
        );

        List<Booking> result = bookingRepository.findItemsNextBookings(
                List.of(item.getId()), now);

        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsNextBookingsItemNotInListShouldBeIgnored() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        Item item1 = itemRepository.save(
                new Item(null, "Item1", "desc1", true, owner, null));
        Item item2 = itemRepository.save(
                new Item(null, "Item2", "desc2", true, owner, null));

        LocalDateTime now = LocalDateTime.now();

        bookingRepository.save(
                new Booking(
                        null,
                        now.plusDays(1),
                        now.plusDays(2),
                        item2,
                        booker,
                        BookingStatus.APPROVED
                )
        );

        List<Booking> result = bookingRepository.findItemsNextBookings(
                List.of(item1.getId()), now);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByBookerIdAndItemIdShouldReturnBooking() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Optional<Booking> result =
                bookingRepository.findByBookerIdAndItemId(
                        booker.getId(),
                        item.getId()
                );

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem().getName(), result.get().getItem().getName());
    }

    @Test
    void findByBookerIdAndItemIdWrongBookerShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        stranger = userRepository.save(stranger);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Optional<Booking> result =
                bookingRepository.findByBookerIdAndItemId(
                        stranger.getId(),
                        item.getId()
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByBookerIdAndItemIdWrongItemShouldReturnEmpty() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

        Item anotherItem = itemRepository.save(
                new Item(null, "Another", "desc", true, owner, null));

        Optional<Booking> result =
                bookingRepository.findByBookerIdAndItemId(
                        booker.getId(),
                        anotherItem.getId()
                );

        assertTrue(result.isEmpty());
    }
}