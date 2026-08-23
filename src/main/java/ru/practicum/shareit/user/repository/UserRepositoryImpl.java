package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new LinkedHashMap<>();
    private Long lastId = 0L;

    @Override
    public User addUser(User user) {
        if (checkEmailExist(null, user)) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }

        user.setId(incrementId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return Collections.unmodifiableList(new ArrayList<>(users.values()));
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(Long id, User user, User updatedUser) {

        if (checkEmailExist(id, user)) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updatedUser.setEmail(user.getEmail());
        }

        return updatedUser;
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    private Long incrementId() {
        return ++lastId;
    }

    private boolean checkEmailExist(Long id, User user) {
        for (User u : users.values()) {
            if (!u.getId().equals(id) && u.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
