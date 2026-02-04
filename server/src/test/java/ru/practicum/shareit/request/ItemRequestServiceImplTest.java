package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.IncomingRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createRequestFromUserExistWorks() {
        IncomingRequestDto dto = new IncomingRequestDto();
        dto.setDescription("request");

        User user = new User();
        user.setId(1L);
        user.setName("TestUser");
        user.setEmail("test@test.ru");

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setId(100L);
        savedRequest.setDescription(dto.getDescription());
        savedRequest.setRequestor(user);
        savedRequest.setCreated(LocalDateTime.now());

        when(userService.findById(1L))
                .thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(savedRequest);

        ItemRequestDto result = itemRequestService.create(1L, dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("request", result.getDescription());

        verify(userService).findById(1L);
        verify(itemRequestRepository).save(any(ItemRequest.class));
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }

    @Test
    void createFromUserNotExistThrowsException() {
        IncomingRequestDto dto = new IncomingRequestDto();
        dto.setDescription("request");

        when(userService.findById(1L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(1L, dto));

        verify(userService).findById(1L);
        verify(itemRequestRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }

    @Test
    void getUserRequestWithUserExistReturnRequests() {
        User user = User.builder()
                .id(10L)
                .build();

        Item item = Item.builder()
                .id(100L)
                .name("Item")
                .owner(user)
                .build();

        List<Item> items = List.of(item);

        ItemRequest request = ItemRequest.builder()
                .id(1000L)
                .description("request")
                .created(LocalDateTime.now())
                .items(items)
                .build();

        List<ItemRequest> requests = List.of(request);

        doNothing().when(userService)
                .validateUserId(1L);
        when(itemRequestRepository.findByRequestorIdWithItems(1L))
                .thenReturn(requests);

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.getFirst().getId());
        assertEquals(request.getDescription(), result.getFirst().getDescription());
        assertEquals(request.getCreated(), result.getFirst().getCreated());
        assertEquals(items.size(), result.getFirst().getItems().size());
        assertEquals(item.getId(), result.getFirst().getItems().getFirst().getId());
        assertEquals(item.getName(), result.getFirst().getItems().getFirst().getName());
        assertEquals(item.getOwner().getId(), result.getFirst().getItems().getFirst().getUserId());

        verify(userService).validateUserId(1L);
        verify(itemRequestRepository).findByRequestorIdWithItems(1L);
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }

    @Test
    void getRequestByIdWithRequestExistReturnsRequest() {
        ItemRequest request = ItemRequest.builder()
                .id(10L)
                .description("request")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        doNothing().when(userService)
                .validateUserId(1L);
        when(itemRequestRepository.findByIdWithItems(10L))
                .thenReturn(Optional.of(request));

        ItemRequestDto result = itemRequestService.getRequestById(1L, 10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("request", result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());

        verify(userService).validateUserId(1L);
        verify(itemRequestRepository).findByIdWithItems(10L);
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }

    @Test
    void getRequestByIdWithRequestNotExistThrowsException() {
        doNothing().when(userService)
                .validateUserId(1L);
        when(itemRequestRepository.findByIdWithItems(10L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 10L));

        verify(userService).validateUserId(1L);
        verify(itemRequestRepository).findByIdWithItems(10L);
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }

    @Test
    void getAllRequestsReturnsRequests() {
        ItemRequestDto request1 = ItemRequestDto.builder()
                .id(10L)
                .description("request1")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        ItemRequestDto request2 = ItemRequestDto.builder()
                .id(20L)
                .description("request2")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        List<ItemRequestDto> requests = List.of(request1, request2);

        doNothing().when(userService)
                .validateUserId(1L);
        when(itemRequestRepository.findAllExceptUser(1L))
                .thenReturn(requests);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request1.getId(), result.getFirst().getId());
        assertEquals(request1.getDescription(), result.getFirst().getDescription());
        assertEquals(request1.getCreated(), result.getFirst().getCreated());

        verify(userService).validateUserId(1L);
        verify(itemRequestRepository).findAllExceptUser(1L);
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }

    @Test
    void findByIdWithRequestExistReturnsRequestWithItems() {
        User user = User.builder()
                .id(10L)
                .build();

        Item item = Item.builder()
                .id(100L)
                .name("Item")
                .owner(user)
                .build();

        List<Item> items = List.of(item);

        ItemRequest request = ItemRequest.builder()
                .id(1000L)
                .description("request")
                .created(LocalDateTime.now())
                .items(items)
                .build();

        when(itemRequestRepository.findByIdWithItems(1000L))
                .thenReturn(Optional.of(request));

        ItemRequest result = itemRequestService.findById(1000L);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertEquals(items.size(), result.getItems().size());
        assertEquals(item.getId(), result.getItems().getFirst().getId());
        assertEquals(item.getName(), result.getItems().getFirst().getName());
        assertEquals(item.getOwner().getId(), result.getItems().getFirst().getOwner().getId());

        verify(itemRequestRepository).findByIdWithItems(1000L);
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }

    @Test
    void findByIdWithRequestNotExistThrowsException() {
        when(itemRequestRepository.findByIdWithItems(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(1L));

        verify(itemRequestRepository).findByIdWithItems(1L);
        verifyNoMoreInteractions(userService, itemRequestRepository);
    }
}