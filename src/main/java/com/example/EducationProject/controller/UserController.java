package com.example.EducationProject.controller;


import com.example.EducationProject.dto.UserDto;
import com.example.EducationProject.entity.User;
import com.example.EducationProject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDto userDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            Map<String, String> errors = new HashMap<>();

            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );

            return ResponseEntity.badRequest().body(errors);
        }

        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

    }


//    @GetMapping
//    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
//        Page<UserDto> users = userService.getAllUsers(pageable);
//        return ResponseEntity.ok(users);
//    }


    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String name,
            Pageable pageable) {

        return ResponseEntity.ok(userService.getUsers(name, pageable));
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }



    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
