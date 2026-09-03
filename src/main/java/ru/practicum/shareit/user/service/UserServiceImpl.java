package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse addUser(UserCreateRequest userCreateRequest) {
        return UserMapper.toUserResponse(userRepository.addUser(UserMapper.toUser(userCreateRequest)));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return UserMapper.toUserResponseList(userRepository.getAllUsers());
    }

    @Override
    public UserResponse getUserResponseById(Long id) {
        return UserMapper.toUserResponse(userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User updatedUser = getUserById(id);

        return UserMapper.toUserResponse(userRepository.updateUser(id, UserMapper.toUser(userRequest), updatedUser));
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);

        userRepository.deleteUser(user);
    }

    private User getUserById(Long id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
