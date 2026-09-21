package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void addUser_shouldReturn200AndUser_whenValid() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Ivan");
        response.setEmail("ivan@mail.com");

        when(userService.addUser(any(UserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@mail.com"));
    }

    @Test
    void addUser_shouldReturn400_whenNameBlank() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("");
        request.setEmail("ivan@mail.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUser_shouldReturn400_whenEmailInvalid() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Ivan");
        request.setEmail("not-an-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        UserResponse first = new UserResponse();
        first.setId(1L);
        first.setName("Ivan");
        first.setEmail("ivan@mail.com");

        when(userService.getAllUsers()).thenReturn(List.of(first));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Ivan");
        response.setEmail("ivan@mail.com");

        when(userService.getUserResponseById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserById_shouldReturn404_whenNotExists() throws Exception {
        when(userService.getUserResponseById(999L)).thenThrow(new NotFoundException("Пользователь с id 999 не найден"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("Ivan Updated");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Ivan Updated");
        response.setEmail("ivan@mail.com");

        when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ivan Updated"));
    }

    @Test
    void updateUser_shouldReturn409_whenEmailAlreadyTaken() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail("taken@mail.com");

        when(userService.updateUser(eq(1L), any(UserRequest.class)))
                .thenThrow(new DuplicateException("Пользователь с таким email уже существует"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}
