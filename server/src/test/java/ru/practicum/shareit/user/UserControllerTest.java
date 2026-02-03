package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShouldReturnUserDtoWhenValidRequest() throws Exception {
        UserDto requestDto = UserDto.builder()
                .name("TestUser")
                .email("test@test.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@test.ru"));
    }

    @Test
    void updateShouldReturnUserDtoWhenValidRequest() throws Exception {
        UserDto savedUser = saveUser();

        UserDto updatedDto = UserDto.builder()
                .id(1L)
                .name("UpdatedUser")
                .email("updated@test.ru")
                .build();

        mockMvc.perform(patch("/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("UpdatedUser"))
                .andExpect(jsonPath("$.email").value("updated@test.ru"));
    }

    @Test
    void findByIdShouldReturnUserDtoWhenValidRequest() throws Exception {
        UserDto savedUser = saveUser();

        mockMvc.perform(get("/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    void deleteUserShouldDeleteUserWhenValidRequest() throws Exception {
        UserDto savedUser = saveUser();

        mockMvc.perform(delete("/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private UserDto saveUser() throws Exception {
        UserDto dto = UserDto.builder()
                .name("TestUser")
                .email("test@test.ru")
                .build();

        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createResponse, UserDto.class);
    }
}