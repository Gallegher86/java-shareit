package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exceptions.EmailAlreadyUsedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserWithNewEmailWorks() {
        User newUser = new User();
        newUser.setName("TestUser");
        newUser.setEmail("test@test.ru");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("TestUser");
        savedUser.setEmail("test@test.ru");

        when(userRepository.existsByEmail("test@test.ru"))
                .thenReturn(false);
        when(userRepository.save(newUser))
                .thenReturn(savedUser);

        User result = userService.create(newUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@test.ru", result.getEmail());

        verify(userRepository).existsByEmail("test@test.ru");
        verify(userRepository).save(newUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserWithExistingEmailThrowsException() {
        User newUser = new User();
        newUser.setEmail("test@test.ru");

        when(userRepository.existsByEmail("test@test.ru"))
                .thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.create(newUser));

        verify(userRepository).existsByEmail("test@test.ru");
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateWithUpdatedUserWorks() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("UpdatedUser");
        updatedUser.setEmail("updated@test.ru");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("TestUser");
        savedUser.setEmail("test@test.ru");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmailAndIdNot("updated@test.ru", 1L))
                .thenReturn(false);

        User result = userService.update(updatedUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("UpdatedUser", result.getName());
        assertEquals("updated@test.ru", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository)
                .existsByEmailAndIdNot("updated@test.ru", 1L);
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateWithNullFieldsDoNothing() {
        User updatedUser = new User();
        updatedUser.setId(1L);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("TestUser");
        savedUser.setEmail("test@test.ru");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(savedUser));

        User result = userService.update(updatedUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TestUser", result.getName());
        assertEquals("test@test.ru", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateWithUserNotExistThrowsException() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("UpdatedUser");
        updatedUser.setEmail("updated@test.ru");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(updatedUser));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateWithExistingEmailThrowsException() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("UpdatedUser");
        updatedUser.setEmail("updated@test.ru");

        User savedUser = new User();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmailAndIdNot("updated@test.ru", 1L))
                .thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.update(updatedUser));

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmailAndIdNot("updated@test.ru", 1L);
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByIdWithUserExistReturnsUser() {
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("TestUser");
        savedUser.setEmail("test@test.ru");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(savedUser));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TestUser", result.getName());
        assertEquals("test@test.ru", result.getEmail());

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByIdWithUserNotExistThrowsException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(1L));

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteWithUserExistWorks() {
        userService.delete(1L);

        verify(userRepository).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteWithUserNotExistThrowsException() {
        doThrow(EmptyResultDataAccessException.class)
                .when(userRepository)
                .deleteById(1L);

        assertThrows(NotFoundException.class,
                () -> userService.delete(1L));

        verify(userRepository).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void validateUserIdWithUserNotExistThrowsException() {
        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> userService.validateUserId(1L));

        verify(userRepository).existsById(1L);
        verifyNoMoreInteractions(userRepository);
    }
}