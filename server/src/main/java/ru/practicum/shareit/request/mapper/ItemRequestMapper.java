package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDtoWithItems(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(
                        itemRequest.getItems() == null
                                ? List.of()
                                : ItemMapper.toRequestDto(itemRequest.getItems())
                )
                .build();
    }

    public static ItemRequest toItemRequest(IncomingRequestDto dto, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(created)
                .build();
    }
}
