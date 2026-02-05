package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.CommentProcessingException;
import ru.practicum.shareit.exceptions.ItemDontBelongToUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
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
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private UserService userService;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void createItemFromUserExistWorks() {
        ItemDto dto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(10L)
                .build();

        User user = new User();
        user.setId(1L);
        user.setName("TestUser");
        user.setEmail("test@test.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);

        Item savedItem = new Item();
        savedItem.setId(100L);
        savedItem.setOwner(user);
        savedItem.setRequest(itemRequest);

        when(userService.findById(1L))
                .thenReturn(user);
        when(itemRequestService.findById(10L))
                .thenReturn(itemRequest);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(savedItem);

        Item result = itemService.create(1L, dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(user.getId(), result.getOwner().getId());
        assertEquals(itemRequest.getId(), result.getRequest().getId());

        verify(userService).findById(1L);
        verify(itemRequestService).findById(10L);
        verify(itemRepository).save(any(Item.class));
        verifyNoMoreInteractions(userService, itemRequestService, itemRepository);
    }

    @Test
    void createItemFromUserNotExistThrowsException() {
        ItemDto dto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(10L)
                .build();

        when(userService.findById(1L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemService.create(1L, dto));

        verify(userService).findById(1L);
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRepository, itemRequestService);
    }

    @Test
    void createItemWithRequestNotExistThrowsException() {
        ItemDto dto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(10L)
                .build();

        User user = new User();
        user.setId(1L);
        user.setName("TestUser");
        user.setEmail("test@test.ru");

        when(userService.findById(1L))
                .thenReturn(user);
        when(itemRequestService.findById(10L))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemService.create(1L, dto));

        verify(userService).findById(1L);
        verify(itemRequestService).findById(10L);
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRepository, itemRequestService);
    }

    @Test
    void updateWithUpdatedItemWorks() {
        User user = new User();
        user.setId(10L);
        user.setName("TestUser");
        user.setEmail("test@test.ru");

        Item updatedItem = Item.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDesc")
                .available(true)
                .owner(user)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(false)
                .owner(user)
                .build();

        doNothing().when(userService)
                .validateUserId(10L);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item result = itemService.update(10L, updatedItem);

        assertNotNull(result);
        assertEquals(updatedItem.getId(), result.getId());
        assertEquals(updatedItem.getName(), result.getName());
        assertEquals(updatedItem.getDescription(), result.getDescription());
        assertEquals(updatedItem.getAvailable(), result.getAvailable());

        verify(userService).validateUserId(10L);
        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void updateWithWrongUserThrowsException() {
        User user = new User();
        user.setId(10L);
        user.setName("TestUser");
        user.setEmail("test@test.ru");

        User wrongUser = new User();
        wrongUser.setId(20L);
        wrongUser.setName("TestUser");
        wrongUser.setEmail("test@test.ru");

        Item updatedItem = Item.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDesc")
                .available(true)
                .owner(user)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(false)
                .owner(wrongUser)
                .build();

        doNothing().when(userService)
                .validateUserId(10L);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThrows(ItemDontBelongToUserException.class,
                () -> itemService.update(10L, updatedItem));

        verify(userService).validateUserId(10L);
        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void updateWithNullFieldsDoNothing() {
        User user = new User();
        user.setId(10L);
        user.setName("TestUser");
        user.setEmail("test@test.ru");

        Item updatedItem = Item.builder()
                .id(1L)
                .owner(user)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(false)
                .owner(user)
                .build();

        doNothing().when(userService)
                .validateUserId(10L);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item result = itemService.update(10L, updatedItem);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());

        verify(userService).validateUserId(10L);
        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void findByIdWithItemExistReturnsItemDto() {
        Long userId = 10L;
        Long itemId = 1L;

        Item item = Item.builder()
                .id(itemId)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(100L)
                .text("comment")
                .build();

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(itemId))
                .thenReturn(List.of());

        ItemDto result = itemService.findById(userId, itemId);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertNotNull(result.getComments()); // проверяем, что comments инициализирован
        assertTrue(result.getComments().isEmpty());

        verify(userService).validateUserId(userId);
        verify(itemRepository).findById(itemId);
        verify(commentRepository).findByItem_Id(itemId);
        verifyNoMoreInteractions(userService, itemRepository, commentRepository);
    }

    @Test
    void findByIdWithItemNotExistThrowsNotFoundException() {
        Long userId = 10L;
        Long itemId = 1L;

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.findById(userId, itemId));

        verify(userService).validateUserId(userId);
        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void findOwnersItemsWithItemsBookingsAndCommentsWorks() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setId(userId);

        Item item = Item.builder()
                .id(10L)
                .name("item")
                .owner(owner)
                .available(true)
                .build();

        List<Item> items = List.of(item);

        Booking lastBooking = Booking.builder()
                .id(100L)
                .item(item)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();

        Booking nextBooking = Booking.builder()
                .id(200L)
                .item(item)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        Comment comment = Comment.builder()
                .id(300L)
                .item(item)
                .author(owner)
                .text("text")
                .build();

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.findAllByOwnerId(userId))
                .thenReturn(items);
        when(bookingRepository.findItemsLastBookings(anyList(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findItemsNextBookings(anyList(), any()))
                .thenReturn(List.of(nextBooking));
        when(commentRepository.findByItem_IdIn(List.of(10L)))
                .thenReturn(List.of(comment));

        List<ItemDto> result = itemService.findOwnersItems(userId);

        assertNotNull(result);
        assertEquals(1, result.size());

        ItemDto dto = result.getFirst();
        assertEquals(item.getId(), dto.getId());
        assertNotNull(dto.getLastBooking());
        assertNotNull(dto.getNextBooking());
        assertEquals(1, dto.getComments().size());

        verify(userService).validateUserId(userId);
        verify(itemRepository).findAllByOwnerId(userId);
        verify(bookingRepository).findItemsLastBookings(anyList(), any());
        verify(bookingRepository).findItemsNextBookings(anyList(), any());
        verify(commentRepository).findByItem_IdIn(List.of(10L));
        verifyNoMoreInteractions(userService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void findOwnersItemsWithNoItemsReturnsEmptyList() {
        Long userId = 1L;

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.findAllByOwnerId(userId))
                .thenReturn(List.of());

        List<ItemDto> result = itemService.findOwnersItems(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService).validateUserId(userId);
        verify(itemRepository).findAllByOwnerId(userId);
        verifyNoMoreInteractions(userService, itemRepository, bookingRepository, commentRepository
        );
    }

    @Test
    void findOwnersItemsWithoutBookingsWorks() {
        Long userId = 1L;

        Item item = Item.builder()
                .id(10L)
                .available(true)
                .build();

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.findAllByOwnerId(userId))
                .thenReturn(List.of(item));
        when(bookingRepository.findItemsLastBookings(anyList(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findItemsNextBookings(anyList(), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItem_IdIn(List.of(10L)))
                .thenReturn(List.of());

        List<ItemDto> result = itemService.findOwnersItems(userId);

        assertEquals(1, result.size());
        ItemDto dto = result.getFirst();

        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertTrue(dto.getComments().isEmpty());
    }

    @Test
    void findOwnersItemsWithoutCommentsWorks() {
        Long userId = 1L;

        Item item = Item.builder()
                .id(10L)
                .available(true)
                .build();

        Booking lastBooking = Booking.builder()
                .id(100L)
                .item(item)
                .build();

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.findAllByOwnerId(userId))
                .thenReturn(List.of(item));
        when(bookingRepository.findItemsLastBookings(anyList(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findItemsNextBookings(anyList(), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItem_IdIn(List.of(10L)))
                .thenReturn(List.of());

        List<ItemDto> result = itemService.findOwnersItems(userId);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getComments().isEmpty());
    }

    @Test
    void findByDescriptionWithValidDescriptionReturnsItems() {
        Long userId = 1L;
        String description = "drill";

        Item item = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.findByDescription(description))
                .thenReturn(List.of(item));

        List<Item> result = itemService.findByDescription(userId, description);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.getFirst().getId());

        verify(userService).validateUserId(userId);
        verify(itemRepository).findByDescription(description);
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void findByDescriptionWithNullDescriptionReturnsEmptyList() {
        Long userId = 1L;

        doNothing().when(userService).validateUserId(userId);

        List<Item> result = itemService.findByDescription(userId, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService).validateUserId(userId);
        verify(itemRepository, never()).findByDescription(any());
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void findByDescriptionWithBlankDescriptionReturnsEmptyList() {
        Long userId = 1L;

        doNothing().when(userService).validateUserId(userId);

        List<Item> result = itemService.findByDescription(userId, "   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService).validateUserId(userId);
        verify(itemRepository, never()).findByDescription(any());
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void createCommentWithValidBookingWorks() {
        Long userId = 1L;
        Long itemId = 2L;

        User user = new User();
        user.setId(userId);

        Item item = Item.builder()
                .id(itemId)
                .build();

        IncomingCommentDto dto = new IncomingCommentDto();
        dto.setText("good item");

        LocalDateTime now = LocalDateTime.now();

        Booking booking = Booking.builder()
                .id(10L)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();

        Comment savedComment = Comment.builder()
                .id(100L)
                .text(dto.getText())
                .item(item)
                .author(user)
                .build();

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndItemId(userId, itemId))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        Comment result = itemService.createComment(userId, itemId, dto);

        assertNotNull(result);
        assertEquals(savedComment.getId(), result.getId());
        assertEquals(dto.getText(), result.getText());

        verify(userService).validateUserId(userId);
        verify(itemRepository).existsById(itemId);
        verify(bookingRepository).findByBookerIdAndItemId(userId, itemId);
        verify(commentRepository).save(any(Comment.class));
        verifyNoMoreInteractions(
                userService,
                itemRepository,
                bookingRepository,
                commentRepository
        );
    }

    @Test
    void createCommentWithoutBookingThrowsException() {
        Long userId = 1L;
        Long itemId = 2L;

        IncomingCommentDto dto = new IncomingCommentDto();
        dto.setText("text");

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndItemId(userId, itemId))
                .thenReturn(Optional.empty());

        assertThrows(CommentProcessingException.class,
                () -> itemService.createComment(userId, itemId, dto));

        verify(userService).validateUserId(userId);
        verify(itemRepository).existsById(itemId);
        verify(bookingRepository).findByBookerIdAndItemId(userId, itemId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createCommentWithNotApprovedBookingThrowsException() {
        Long userId = 1L;
        Long itemId = 2L;

        User user = new User();
        user.setId(userId);

        Item item = Item.builder().id(itemId).build();

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().minusDays(1))
                .build();

        IncomingCommentDto dto = new IncomingCommentDto();
        dto.setText("text");

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndItemId(userId, itemId))
                .thenReturn(Optional.of(booking));

        assertThrows(CommentProcessingException.class,
                () -> itemService.createComment(userId, itemId, dto));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void createCommentWithNotFinishedBookingThrowsException() {
        Long userId = 1L;
        Long itemId = 2L;

        User user = new User();
        user.setId(userId);

        Item item = Item.builder().id(itemId).build();

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .end(LocalDateTime.now().plusDays(1)) // ещё не закончено
                .build();

        IncomingCommentDto dto = new IncomingCommentDto();
        dto.setText("text");

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndItemId(userId, itemId))
                .thenReturn(Optional.of(booking));

        assertThrows(CommentProcessingException.class,
                () -> itemService.createComment(userId, itemId, dto));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void createCommentWithNotExistingItemThrowsException() {
        Long userId = 1L;
        Long itemId = 2L;

        IncomingCommentDto dto = new IncomingCommentDto();
        dto.setText("text");

        doNothing().when(userService).validateUserId(userId);
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(userId, itemId, dto));

        verify(userService).validateUserId(userId);
        verify(itemRepository).existsById(itemId);
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getByIdWithExistingItemReturnsItem() {
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("Drill")
                .description("Power drill")
                .available(true)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getById(itemId);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());

        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByIdWithNonExistingItemThrowsNotFoundException() {
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId));

        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }
}