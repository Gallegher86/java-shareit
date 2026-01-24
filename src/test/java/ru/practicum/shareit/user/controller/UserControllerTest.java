package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUser() throws Exception {
        UserDto inputDto = UserDto.builder()
                .name("User")
                .email("user@mail.com")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();

        when(userService.create(any(User.class)))
                .thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;

        UpdatedUserDto updatedDto = UpdatedUserDto.builder()
                .name("Updated name")
                .email("updated@mail.com")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .name("Updated name")
                .email("updated@mail.com")
                .build();

        when(userService.update(any(User.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.email").value("updated@mail.com"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .name("User")
                .email("user@mail.com")
                .build();

        when(userService.findById(userId))
                .thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }

    @Test
    void shouldFailWhenNameIsNull() throws Exception {
        UserDto dto = UserDto.builder()
                .email("user@mail.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenNameIsBlank() throws Exception {
        UserDto dto = UserDto.builder()
                .name("")
                .email("user@mail.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenEmailIsNull() throws Exception {
        UserDto dto = UserDto.builder()
                .name("User")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenEmailIsInvalid() throws Exception {
        UserDto dto = UserDto.builder()
                .name("User")
                .email("not-an-email")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}