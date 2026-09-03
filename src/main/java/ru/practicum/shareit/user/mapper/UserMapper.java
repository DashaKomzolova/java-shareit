package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class UserMapper {

    public static User toUser(UserCreateRequest userCreateRequest) {
        User user = new User();
        user.setName(userCreateRequest.getName());
        user.setEmail(userCreateRequest.getEmail());
        return user;
    }

    public static User toUser(UserRequest userRequest) {
        User user = new User();
        user.setId(userRequest.getId());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        return user;
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        return userResponse;
    }

    public static List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }
}
