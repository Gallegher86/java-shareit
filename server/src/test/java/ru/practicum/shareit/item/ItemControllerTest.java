package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
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
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Test
    void createShouldReturnItemDtoWhenValidRequest() throws Exception {
        User user = new User();
        user.setName("owner");
        user.setEmail("owner@test.com");
        em.persist(user);
        em.flush();
        em.clear();

        ItemDto dto = ItemDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateShouldReturnUpdatedItemDtoWhenValidRequest() throws Exception {
        User user = new User();
        user.setName("owner");
        user.setEmail("owner@test.com");
        em.persist(user);

        Item item = Item.builder()
                .name("Drill")
                .description("Old description")
                .available(true)
                .owner(user)
                .build();
        em.persist(item);
        em.flush();
        em.clear();

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Drill")
                .description("New description")
                .available(false)
                .build();

        mockMvc.perform(patch("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value("Updated Drill"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void findByIdShouldReturnItemDtoWithComments() throws Exception {
        User user = new User();
        user.setName("owner");
        user.setEmail("owner@test.com");
        em.persist(user);

        Item item = Item.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(user)
                .build();
        em.persist(item);

        Comment comment = Comment.builder()
                .text("Great item!")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        em.persist(comment);
        em.flush();
        em.clear();

        mockMvc.perform(get("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments[0].text").value("Great item!"))
                .andExpect(jsonPath("$.comments[0].authorName").value(user.getName()));
    }

    @Test
    void findOwnersItemsShouldReturnItemsWithoutCommentsOrBookings() throws Exception {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        em.persist(owner);

        Item item1 = Item.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item1);

        Item item2 = Item.builder()
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item2);
        em.flush();
        em.clear();

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()));
    }

    @Test
    void findByDescriptionShouldReturnItemsMatchingText() throws Exception {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        em.persist(owner);

        Item item1 = Item.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item1);

        Item item2 = Item.builder()
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item2);
        em.flush();
        em.clear();

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("text", "drill")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[0].description").value("Power drill"));
    }

    @Test
    void postCommentShouldReturnCommentDto() throws Exception {
        User user = new User();
        user.setName("Commenter");
        user.setEmail("commenter@test.com");
        em.persist(user);

        Item item = Item.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(user)
                .build();
        em.persist(item);

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(booking);
        em.flush();
        em.clear();

        IncomingCommentDto commentDto = new IncomingCommentDto();
        commentDto.setText("Great item!");

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value(user.getName()))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created").exists());
    }
}