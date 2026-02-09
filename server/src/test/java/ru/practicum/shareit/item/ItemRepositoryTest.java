package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByOwnerIdReturnsCorrectItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");

        userRepository.saveAll(List.of(owner, user));

        Item item1 = Item.builder().name("Drill").description("Power drill").available(true).owner(owner).build();
        Item item2 = Item.builder().name("Hammer").description("Steel hammer").available(true).owner(owner).build();
        Item item3 = Item.builder().name("Screwdriver").description("Electric screwdriver").available(true).owner(user).build();

        itemRepository.saveAll(List.of(item1, item2, item3));

        List<Item> result = itemRepository.findAllByOwnerId(owner.getId());

        assertThat(result).hasSize(2)
                .extracting(Item::getId)
                .containsExactlyInAnyOrder(item1.getId(), item2.getId());
    }

    @Test
    void findByDescriptionReturnsMatchingAvailableItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        userRepository.save(owner);

        Item item1 = Item.builder().name("Drill").description("Power drill").available(true).owner(owner).build();
        Item item2 = Item.builder().name("Hammer").description("Steel hammer").available(true).owner(owner).build();
        Item item3 = Item.builder().name("Drill").description("Old drill").available(false).owner(owner).build();

        itemRepository.saveAll(List.of(item1, item2, item3));

        List<Item> result = itemRepository.findByDescription("drill");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Drill");
        assertThat(result.getFirst().getAvailable()).isTrue();
    }

    @Test
    void findByDescriptionIsCaseInsensitive() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        userRepository.save(owner);

        Item item = Item.builder().name("Drill").description("Power Drill").available(true).owner(owner).build();
        itemRepository.save(item);

        List<Item> result = itemRepository.findByDescription("DRILL");

        assertThat(result).hasSize(1)
                .extracting(Item::getName)
                .containsExactly("Drill");
    }
}