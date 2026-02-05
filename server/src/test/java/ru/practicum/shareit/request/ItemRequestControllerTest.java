package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Test
    void createShouldReturnItemRequestDtoWhenValidRequest() throws Exception {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@test.ru");
        em.persist(requestor);
        em.flush();
        em.clear();

        IncomingRequestDto dto = new IncomingRequestDto();
        dto.setDescription("desc");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getUserRequestsShouldReturnItemRequestDtoWhenValidRequest() throws Exception {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@test.ru");
        em.persist(requestor);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@test.ru.ru");
        em.persist(owner);

        ItemRequest request = new ItemRequest();
        request.setDescription("desc");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        em.persist(request);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setRequest(request);
        item.setOwner(owner);
        em.persist(item);
        em.flush();
        em.clear();

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(request.getId()))
                .andExpect(jsonPath("$[0].description").value(request.getDescription()))
                .andExpect(jsonPath("$[0].created").exists())

                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].id").value(item.getId()))
                .andExpect(jsonPath("$[0].items[0].name").value(item.getName()))
                .andExpect(jsonPath("$[0].items[0].userId").value(owner.getId()));
    }

    @Test
    void getRequestByIdShouldReturnItemRequestDtoWhenValidRequest() throws Exception {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@test.ru");
        em.persist(requestor);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@test.ru.ru");
        em.persist(owner);

        ItemRequest request = new ItemRequest();
        request.setDescription("desc");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        em.persist(request);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setRequest(request);
        item.setOwner(owner);
        em.persist(item);
        em.flush();
        em.clear();

        mockMvc.perform(get("/requests/{id}", request.getId())
                        .header("X-Sharer-User-Id", requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.created").exists())

                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value(item.getId()))
                .andExpect(jsonPath("$.items[0].name").value(item.getName()))
                .andExpect(jsonPath("$.items[0].userId").value(owner.getId()));
    }

    @Test
    void getAllRequestsShouldReturnItemRequestsDtoWhenValidRequest() throws Exception {
        User requestor1 = new User();
        requestor1.setName("requestor1");
        requestor1.setEmail("requestor1@test.ru");
        em.persist(requestor1);

        User requestor2 = new User();
        requestor2.setName("requestor2");
        requestor2.setEmail("requestor2@test.ru");
        em.persist(requestor2);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("desc1");
        request1.setRequestor(requestor1);
        request1.setCreated(LocalDateTime.now());
        em.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("desc2");
        request2.setRequestor(requestor2);
        request2.setCreated(LocalDateTime.now());
        em.persist(request2);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setRequest(request2);
        item.setOwner(requestor2);
        em.persist(item);
        em.flush();
        em.clear();

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requestor1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(request2.getId()))
                .andExpect(jsonPath("$[0].description").value(request2.getDescription()))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].items").value(Matchers.nullValue()));
    }
}