package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUser_fromCreateRequest_shouldMapNameAndEmail() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Иван");
        request.setEmail("ivan@mail.com");

        User user = UserMapper.toUser(request);

        assertThat(user.getName()).isEqualTo("Иван");
        assertThat(user.getEmail()).isEqualTo("ivan@mail.com");
    }

    @Test
    void toUser_fromUpdateRequest_shouldMapIdNameAndEmail() {
        UserRequest request = new UserRequest();
        request.setId(3L);
        request.setName("Пётр");
        request.setEmail("petr@mail.com");

        User user = UserMapper.toUser(request);

        assertThat(user.getId()).isEqualTo(3L);
        assertThat(user.getName()).isEqualTo("Пётр");
        assertThat(user.getEmail()).isEqualTo("petr@mail.com");
    }

    @Test
    void toUserResponse_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@mail.com");

        UserResponse response = UserMapper.toUserResponse(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Иван");
        assertThat(response.getEmail()).isEqualTo("ivan@mail.com");
    }

    @Test
    void toUserResponseList_shouldMapEachUserInList() {
        User first = new User();
        first.setId(1L);
        User second = new User();
        second.setId(2L);

        List<UserResponse> result = UserMapper.toUserResponseList(List.of(first, second));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }
}
