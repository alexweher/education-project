package com.example.EducationProject.controller;


import com.example.EducationProject.dto.UserDto;
import com.example.EducationProject.exception.UserNotFoundException;
import com.example.EducationProject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {

        UserDto requestDto = new UserDto();
        requestDto.setName("Alex");
        requestDto.setEmail("alex@mail.com");
        requestDto.setPassword("12345");
        requestDto.setRoles("ROLE_USER");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Alex");
        responseDto.setEmail("alex@mail.com");
        responseDto.setRoles("ROLE_USER");

        when(userService.createUser(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.email").value("alex@mail.com"))
                .andExpect(jsonPath("$.roles").value("ROLE_USER"));
    }


    @Test
    void createUser_shouldReturnBadRequest_whenValidationFails() throws Exception {

        UserDto requestDto = new UserDto();
        requestDto.setName("A");
        requestDto.setEmail("not-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.status").value(400));
    }


    @Test
    void getUserById_shouldReturnUser() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Alex");
        userDto.setEmail("alex@mail.com");
        userDto.setRoles("ROLE_USER");

        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.email").value("alex@mail.com"))
                .andExpect(jsonPath("$.roles").value("ROLE_USER"));
    }


    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {

        when(userService.getUserById(1L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.status").value(404));
    }


    @Test
    void getUsers_shouldReturnPageOfUsers() throws Exception {

        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@mail.com");
        user.setRoles("ROLE_USER");

        PageImpl<UserDto> page = new PageImpl<>(
                List.of(user),
                PageRequest.of(0, 10),
                1
        );

        when(userService.getUsers(eq(null), any()))
                .thenReturn(page);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Alex"))
                .andExpect(jsonPath("$.totalElements").value(1));

    }


    @Test
    void getUsers_shouldFilterByName() throws Exception {

        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@mail.com");
        user.setRoles("ROLE_USER");

        PageImpl<UserDto> page = new PageImpl<>(
                List.of(user),
                PageRequest.of(0, 10),
                1
        );

        when(userService.getUsers(eq("Alex"), any()))
                .thenReturn(page);

        mockMvc.perform(get("/users")
                        .param("name", "Alex")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Alex"));

    }

    @Test
    void getUsers_shouldReturnEmptyPage() throws Exception {

        PageImpl<UserDto> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );

        when(userService.getUsers(eq(null), any()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {

        Long userId = 1L;

        UserDto requestDto = new UserDto();
        requestDto.setName("New Name");
        requestDto.setEmail("new@mail.com");

        UserDto responseDto = new UserDto();
        responseDto.setId(userId);
        responseDto.setName("New Name");
        responseDto.setEmail("new@mail.com");
        responseDto.setRoles("ROLE_USER");

        when(userService.updateUser(eq(userId), any(UserDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }


    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {

        Long userId = 1L;

        UserDto requestDto = new UserDto();
        requestDto.setName("New Name");
        requestDto.setEmail("new@mail.com");

        when(userService.updateUser(eq(userId), any(UserDto.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.status").value(404));
    }


    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {

        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}