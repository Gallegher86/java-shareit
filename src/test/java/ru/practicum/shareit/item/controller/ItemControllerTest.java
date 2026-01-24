package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateItem() throws Exception {
        Long userId = 1L;

        ItemDto inputDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        Item savedItem = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(itemService.create(eq(userId), any(Item.class)))
                .thenReturn(savedItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 10L;

        UpdatedItemDto updatedDto = UpdatedItemDto.builder()
                .name("Updated drill")
                .description("Updated description")
                .available(false)
                .build();

        Item updatedItem = Item.builder()
                .id(itemId)
                .name("Updated drill")
                .description("Updated description")
                .available(false)
                .build();

        when(itemService.update(eq(userId), any(Item.class)))
                .thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated drill"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void shouldReturnItemById() throws Exception {
        Long userId = 1L;
        Long itemId = 5L;

        Item item = Item.builder()
                .id(itemId)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(itemService.findById(userId, itemId))
                .thenReturn(item);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldReturnOwnersItems() throws Exception {
        Long userId = 1L;

        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of())
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Hammer")
                .description("Heavy hammer")
                .available(false)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of())
                .build();

        when(itemService.findOwnersItems(userId))
                .thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Hammer"));
    }

    @Test
    void shouldSearchItemsByDescription() throws Exception {
        Long userId = 1L;
        String text = "drill";

        Item item1 = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Mini drill")
                .description("Small electric drill")
                .available(true)
                .build();

        when(itemService.findByDescription(userId, text))
                .thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Mini drill"));
    }

    @Test
    void shouldFailWhenNameIsNull() throws Exception {
        ItemDto dto = ItemDto.builder()
                .description("desc")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    void shouldFailWhenNameIsBlank() throws Exception {
        ItemDto dto = ItemDto.builder()
                .name("")
                .description("desc")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    void shouldFailWhenDescriptionIsNull() throws Exception {
        ItemDto dto = ItemDto.builder()
                .name("Drill")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenAvailableIsNull() throws Exception {
        ItemDto dto = ItemDto.builder()
                .name("Drill")
                .description("desc")
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}