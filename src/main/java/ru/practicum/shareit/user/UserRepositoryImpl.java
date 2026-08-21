package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
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
    public User getUserById(Long id) {
        User foundUser = users.get(id);

        if (foundUser == null) {
            throw new NotFoundException("Пользователя с таким id не существует");
        } else {
            return foundUser;
        }
    }

    @Override
    public User updateUser(Long id, User user) {
        User foundUser = getUserById(id);

        if (checkEmailExist(id, user)) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            foundUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            foundUser.setEmail(user.getEmail());
        }

        return foundUser;
    }

    @Override
    public void deleteUserById(Long id) {
        User foundUser = users.get(id);

        if (foundUser == null) {
            throw new NotFoundException("Пользователя с таким id не существует");
        } else {
            users.remove(foundUser.getId());
        }
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
