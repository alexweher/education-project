package com.example.EducationProject.controller;


import com.example.EducationProject.dto.UserDto;

import com.example.EducationProject.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===== CREATE =====
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserDto userDto) {

        log.info("HTTP POST /users - Creating user with email: {}", userDto.getEmail());

        UserDto createdUser = userService.createUser(userDto);

        log.info("HTTP POST /users - User created with id: {}", createdUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // ===== GET WITH FILTER =====
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String name,
            Pageable pageable) {

        log.info("HTTP GET /users - name filter: {}, page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize());

        Page<UserDto> result = userService.getUsers(name, pageable);

        log.info("HTTP GET /users - Returned {} users", result.getTotalElements());

        return ResponseEntity.ok(result);
    }

    // ===== GET BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {

        log.info("HTTP GET /users/{} - Fetching user", id);

        UserDto user = userService.getUserById(id);

        log.info("HTTP GET /users/{} - Success", id);

        return ResponseEntity.ok(user);
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @RequestBody UserDto userDto) {

        log.info("HTTP PUT /users/{} - Updating user", id);

        UserDto updatedUser = userService.updateUser(id, userDto);

        log.info("HTTP PUT /users/{} - Updated successfully", id);

        return ResponseEntity.ok(updatedUser);
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        log.info("HTTP DELETE /users/{} - Deleting user", id);

        userService.deleteUser(id);

        log.info("HTTP DELETE /users/{} - Deleted successfully", id);

        return ResponseEntity.noContent().build();
    }
}