package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    private UserCreateRequest makeCreateRequest(String name, String email) {
        UserCreateRequest request = new UserCreateRequest();
        request.setName(name);
        request.setEmail(email);
        return request;
    }

    @Test
    void addUser_shouldSaveAndReturnUserWithId() {
        UserResponse response = userService.addUser(makeCreateRequest("Ivan", "ivan1@mail.com"));

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Ivan");
        assertThat(response.getEmail()).isEqualTo("ivan1@mail.com");
    }

    @Test
    void addUser_shouldThrowDuplicateException_whenEmailAlreadyExists() {
        userService.addUser(makeCreateRequest("Ivan", "dup@mail.com"));

        assertThrows(DuplicateException.class,
                () -> userService.addUser(makeCreateRequest("Petr", "dup@mail.com")));
    }

    @Test
    void getAllUsers_shouldReturnAllSavedUsers() {
        userService.addUser(makeCreateRequest("Ivan", "ivan2@mail.com"));
        userService.addUser(makeCreateRequest("Petr", "petr2@mail.com"));

        List<UserResponse> users = userService.getAllUsers();

        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void getUserResponseById_shouldReturnUser_whenExists() {
        UserResponse created = userService.addUser(makeCreateRequest("Ivan", "ivan3@mail.com"));

        UserResponse found = userService.getUserResponseById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getEmail()).isEqualTo("ivan3@mail.com");
    }

    @Test
    void getUserResponseById_shouldThrowNotFoundException_whenNotExists() {
        assertThrows(NotFoundException.class, () -> userService.getUserResponseById(999999L));
    }

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        UserResponse created = userService.addUser(makeCreateRequest("Ivan", "ivan4@mail.com"));

        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("Ivan Updated");
        updateRequest.setEmail("ivan4updated@mail.com");

        UserResponse updated = userService.updateUser(created.getId(), updateRequest);

        assertThat(updated.getName()).isEqualTo("Ivan Updated");
        assertThat(updated.getEmail()).isEqualTo("ivan4updated@mail.com");
    }

    @Test
    void updateUser_shouldThrowDuplicateException_whenNewEmailBelongsToAnotherUser() {
        userService.addUser(makeCreateRequest("Ivan", "taken@mail.com"));
        UserResponse second = userService.addUser(makeCreateRequest("Petr", "petr5@mail.com"));

        UserRequest updateRequest = new UserRequest();
        updateRequest.setEmail("taken@mail.com");

        assertThrows(DuplicateException.class, () -> userService.updateUser(second.getId(), updateRequest));
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        UserResponse created = userService.addUser(makeCreateRequest("Ivan", "ivan6@mail.com"));

        userService.deleteUser(created.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserResponseById(created.getId()));
    }

    @Test
    void getUserById_shouldReturnUserEntity_whenExists() {
        UserResponse created = userService.addUser(makeCreateRequest("Ivan", "ivan7@mail.com"));

        User user = userService.getUserById(created.getId());

        assertThat(user.getId()).isEqualTo(created.getId());
    }

    @Test
    void getUserById_shouldThrowNotFoundException_whenNotExists() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999999L));
    }
}
