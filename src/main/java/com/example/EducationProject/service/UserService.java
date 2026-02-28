package com.example.EducationProject.service;

import com.example.EducationProject.dto.UserDto;
import com.example.EducationProject.entity.User;
import com.example.EducationProject.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    // ===== CREATE =====
    public UserDto createUser(UserDto userDto) {
        User user = mapToEntity(userDto);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

//    // ===== GET ALL =====
//    public Page<UserDto> getAllUsers(Pageable pageable) {
//        return userRepository.findAll(pageable)
//                .map(this::mapToDto);
//    }


    public Page<UserDto> getUsers(String name, Pageable pageable) {

        Page<User> page;

        if (name != null && !name.isBlank()) {
            page = userRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        return page.map(this::mapToDto);
    }


    // ===== GET BY ID =====
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    // ===== UPDATE =====
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return mapToDto(userRepository.save(user));
    }



    // ===== DELETE =====
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    // =============================
    // 🔹 Мапперы
    // =============================

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private User mapToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}

