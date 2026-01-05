package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
public class UpdatedItemDto {
    private String name;
    private String description;
    private Boolean available;
}
