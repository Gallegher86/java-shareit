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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

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
        UserDto requestor = createUser("requestor", "requestor@test.ru");

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
        UserDto requestor = createUser("requestor", "requestor@test.ru");
        UserDto owner = createUser("owner", "owner@test.ru");

        ItemRequestDto request = createRequest(requestor.getId());

        ItemDto item = createItem(owner.getId(), request.getId());

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
        UserDto requestor = createUser("requestor", "requestor@test.ru");
        UserDto owner = createUser("owner", "owner@test.ru");

        ItemRequestDto request = createRequest(requestor.getId());

        ItemDto item = createItem(owner.getId(), request.getId());

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
        UserDto requestor1 = createUser("requestor1", "requestor1@test.ru");
        UserDto requestor2 = createUser("requestor2", "requestor2@test.ru");

        ItemRequestDto request1 = createRequest(requestor1.getId());
        ItemRequestDto request2 = createRequest(requestor2.getId());

        ItemDto item = createItem(requestor2.getId(), request2.getId());

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

    private UserDto createUser(String name, String email) throws Exception {
        UserDto requestor = UserDto.builder()
                .name(name)
                .email(email)
                .build();

        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestor)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createResponse, UserDto.class);
    }

    private ItemDto createItem(Long userId, Long requestId) throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(requestId)
                .build();

        String createResponse = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createResponse, ItemDto.class);
    }

    private ItemRequestDto createRequest(Long userId) throws Exception {
        IncomingRequestDto dto = new IncomingRequestDto();
        dto.setDescription("desc");

        String createResponse = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createResponse, ItemRequestDto.class);
    }
}