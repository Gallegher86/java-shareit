package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static List<ItemDto> toItemsDto(List<Item> items, Map<Long, Booking> lastBookingMap,
                                           Map<Long, Booking> nextBookingMap, Map<Long, List<CommentDto>> commentsMap) {
        return items.stream()
                .map(item -> {
                    ItemDto dto = toItemDto(item);

                    Booking last = lastBookingMap.get(item.getId());
                    Booking next = nextBookingMap.get(item.getId());

                    dto.setLastBooking(last != null ? last.getEnd() : null);
                    dto.setNextBooking(next != null ? next.getStart() : null);
                    dto.setComments(commentsMap.getOrDefault(item.getId(), List.of()));

                    return dto;
                })
                .toList();
    }

    public static List<ItemDtoForRequestDto> toRequestDto(List<Item> items) {
        return items.stream().map(item -> ItemDtoForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .userId(item.getOwner().getId())
                .build()).toList();
    }
}
