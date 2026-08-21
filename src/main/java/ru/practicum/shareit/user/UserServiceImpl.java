package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.addUser(UserMapper.toUser(userDto)));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userRepository.getAllUsers());
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        return UserMapper.toUserDto(userRepository.updateUser(id, UserMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }
}
