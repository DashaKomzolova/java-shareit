package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
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
        if (userRepository.existsByEmail(userCreateRequest.getEmail())) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }

        return UserMapper.toUserResponse(userRepository.save(UserMapper.toUser(userCreateRequest)));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return UserMapper.toUserResponseList(userRepository.findAll());
    }

    @Override
    public UserResponse getUserResponseById(Long id) {
        return UserMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User updatedUser = getUserById(id);

        if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
            if (userRepository.existsByEmailAndIdNot(userRequest.getEmail(), id)) {
                throw new DuplicateException("Пользователь с таким email уже существует");
            }
            updatedUser.setEmail(userRequest.getEmail());
        }

        if (userRequest.getName() != null && !userRequest.getName().isBlank()) {
            updatedUser.setName(userRequest.getName());
        }

        return UserMapper.toUserResponse(userRepository.save(updatedUser));
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);

        userRepository.delete(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
