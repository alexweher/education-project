package com.example.EducationProject.service;

import com.example.EducationProject.controller.UserController;
import com.example.EducationProject.dto.UserDto;
import com.example.EducationProject.entity.User;
import com.example.EducationProject.exception.UserNotFoundException;
import com.example.EducationProject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // ===== CREATE =====
    public UserDto createUser(UserDto userDto) {

        log.info("Creating new user with email: {}", userDto.getEmail());

        User user = mapToEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);

        log.info("User created successfully. ID: {}", savedUser.getId());

        return mapToDto(savedUser);
    }

    // ===== GET WITH FILTER =====
    public Page<UserDto> getUsers(String name, Pageable pageable) {

        log.info("Fetching users. Filter by name: {}", name);

        Page<User> page;

        if (name != null && !name.isBlank()) {
            page = userRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        log.info("Users fetched. Total elements: {}", page.getTotalElements());

        return page.map(this::mapToDto);
    }

    // ===== GET BY ID =====
    public UserDto getUserById(Long id) {

        log.info("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found");
                });

        return mapToDto(user);
    }

    // ===== UPDATE =====
    public UserDto updateUser(Long id, UserDto userDto) {

        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update. ID: {}", id);
                    return new UserNotFoundException("User not found");
                });

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully. ID: {}", id);

        return mapToDto(updatedUser);
    }

    // ===== DELETE =====
    public void deleteUser(Long id) {

        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion. ID: {}", id);
            throw new UserNotFoundException("User not found");
        }

        userRepository.deleteById(id);

        log.info("User deleted successfully. ID: {}", id);
    }


    // =============================
    // 🔹 Мапперы
    // =============================

    private UserDto mapToDto(User user) {

        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());

        return dto;
    }

    private User mapToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRoles(dto.getRoles());
        return user;
    }
}

