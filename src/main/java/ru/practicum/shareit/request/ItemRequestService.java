package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, IncomingRequestDto dto);

    List<ItemRequestDto> getUserRequests(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequest findById(Long requestId);
}
