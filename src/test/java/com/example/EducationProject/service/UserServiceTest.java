package com.example.EducationProject.service;

import com.example.EducationProject.dto.UserDto;
import com.example.EducationProject.entity.User;
import com.example.EducationProject.exception.UserNotFoundException;
import com.example.EducationProject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ===== CREATE =====
    @Test
    void createUser_shouldSaveUser() {

        UserDto dto = new UserDto();
        dto.setName("Alex");
        dto.setEmail("alex@mail.com");
        dto.setPassword("12345");

        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@mail.com");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(dto);

        assertEquals("Alex", result.getName());
        assertEquals(1L, result.getId());
        verify(userRepository).save(any(User.class));
    }

    // ===== GET BY ID =====
    @Test
    void getUserById_shouldReturnUser() {

        User user = new User();
        user.setId(1L);
        user.setName("Alex");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals("Alex", result.getName());
    }

    @Test
    void getUserById_shouldThrowExceptionWhenNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1L));
    }

    // ===== GET USERS =====
    @Test
    void getUsers_shouldFilterByName() {

        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(1L);
        user.setName("Alex");

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findByNameContainingIgnoreCase("Alex", pageable))
                .thenReturn(page);

        Page<UserDto> result = userService.getUsers("Alex", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Alex", result.getContent().get(0).getName());

        verify(userRepository).findByNameContainingIgnoreCase("Alex", pageable);
    }

    @Test
    void getUsers_shouldReturnAllUsersWhenNameIsNull() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(List.of());

        when(userRepository.findAll(pageable)).thenReturn(page);

        userService.getUsers(null, pageable);

        verify(userRepository).findAll(pageable);
    }

    @Test
    void getUsers_shouldReturnAllUsersWhenNameIsBlank() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(List.of());

        when(userRepository.findAll(pageable)).thenReturn(page);

        userService.getUsers("", pageable);

        verify(userRepository).findAll(pageable);
    }

    // ===== UPDATE =====
    @Test
    void updateUser_shouldUpdateUser() {

        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@mail.com");

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.updateUser(userId, updateDto);

        assertEquals("New Name", result.getName());
        assertEquals("new@mail.com", result.getEmail());

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(1L, new UserDto()));
    }

    // ===== DELETE =====
    @Test
    void deleteUser_shouldCallRepository() {

        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L));

        verify(userRepository, never()).deleteById(any());
    }
}