package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponse addUser(UserCreateRequest userCreateRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUserResponseById(Long id);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    User getUserById(Long id);
}
