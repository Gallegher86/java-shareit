package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, IncomingRequestDto dto) {
        User requestor = userService.findById(userId);
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(dto, requestor, created));
        log.info("Запрос на вещь с id {} добавлен в список.", itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userService.validateUserId(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdWithItems(userId);
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDtoWithItems).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.validateUserId(userId);
        ItemRequest itemRequest = findById(requestId);
        log.info("Запрос на вещь с id {} найден.", requestId);
        return ItemRequestMapper.toItemRequestDtoWithItems(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userService.validateUserId(userId);
        return itemRequestRepository.findAllExceptUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest findById(Long requestId) {
        return itemRequestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Запрос на вещь c id %d не найден.", requestId)));
    }
}
