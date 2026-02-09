package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    User owner;
    User booker;
    Item item;
    LocalDateTime now;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@mail.com");
        booker = new User(null, "Booker", "booker@mail.com");
        em.persist(owner);
        em.persist(booker);

        item = new Item(null, "Drill", "desc", true, owner, null);
        em.persist(item);
        em.flush();
        em.clear();

        now = LocalDateTime.now();
    }

    @Test
    void createBookingShouldReturnBookingDto() throws Exception {
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()))
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void approveBookingShouldReturnUpdatedBookingDto() throws Exception {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        em.flush();
        em.clear();

        mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()));
    }

    @Test
    void getBookingShouldReturnBookingDto() throws Exception {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();
        em.clear();

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()));
    }

    @Test
    void getBookerBookingsShouldReturnAllBookingsForUser() throws Exception {
        Booking booking1 = new Booking(null, now.plusDays(1), now.plusDays(2), item, booker, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.minusDays(2), now.minusDays(1), item, booker, BookingStatus.WAITING);
        em.persist(booking1);
        em.persist(booking2);
        em.flush();
        em.clear();

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].booker.id").value(booker.getId()))
                .andExpect(jsonPath("$[1].booker.id").value(booker.getId()));
    }

    @Test
    void getOwnerBookingsShouldReturnAllBookingsForOwner() throws Exception {
        Booking booking1 = new Booking(null, now.plusDays(1), now.plusDays(2), item, booker, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.minusDays(2), now.minusDays(1), item, booker, BookingStatus.WAITING);
        em.persist(booking1);
        em.persist(booking2);
        em.flush();
        em.clear();

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("WAITING"));
    }
}