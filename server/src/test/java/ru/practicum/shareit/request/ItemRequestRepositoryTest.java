package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findByRequestorIdWithItemsReturnsRequestsWithItems() {
        User user = em.persist(User.builder()
                .name("User")
                .email("user@test.ru")
                .build());

        Item item = em.persist(Item.builder()
                .name("Item")
                .description("desc")
                .available(true)
                .owner(user)
                .build());

        ItemRequest request = em.persist(ItemRequest.builder()
                .description("request")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());

        item.setRequest(request);
        em.persist(item);

        em.flush();
        em.clear();

        List<ItemRequest> result =
                itemRequestRepository.findByRequestorIdWithItems(user.getId());

        assertEquals(1, result.size());
        ItemRequest savedRequest = result.getFirst();

        assertEquals(request.getId(), savedRequest.getId());
        assertEquals(1, savedRequest.getItems().size());
        assertEquals(item.getId(), savedRequest.getItems().getFirst().getId());
    }

    @Test
    void findByIdWithItemsReturnsRequestWithItems() {
        User user = em.persist(User.builder()
                .name("User")
                .email("user@test.ru")
                .build());

        ItemRequest request = em.persist(ItemRequest.builder()
                .description("request")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());

        Item item = em.persist(Item.builder()
                .name("Item")
                .description("desc")
                .available(true)
                .owner(user)
                .request(request)
                .build());

        em.flush();
        em.clear();

        Optional<ItemRequest> result =
                itemRequestRepository.findByIdWithItems(request.getId());

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getItems().size());
        assertEquals(item.getId(), result.get().getItems().getFirst().getId());
    }

    @Test
    void findAllExceptUserReturnsAllRequests() {
        User user = em.persist(User.builder()
                .name("User")
                .email("user@test.ru")
                .build());

        User otherUser = em.persist(User.builder()
                .name("OtherUser")
                .email("userOther@test.ru")
                .build());

        ItemRequest request = em.persist(ItemRequest.builder()
                .description("userRequest")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());

        ItemRequest otherRequest = em.persist(ItemRequest.builder()
                .description("otherUserRequest")
                .created(LocalDateTime.now())
                .requestor(otherUser)
                .build());

        em.flush();
        em.clear();

        List<ItemRequestDto> result =
                itemRequestRepository.findAllExceptUser(user.getId());

        assertEquals(1, result.size());
        ItemRequestDto savedRequest = result.getFirst();

        assertEquals(otherRequest.getId(), savedRequest.getId());
        assertEquals(otherRequest.getDescription(), savedRequest.getDescription());
    }
}