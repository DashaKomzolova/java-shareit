package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemRepository.addItem(ItemMapper.toItem(userRepository.getUserById(userId),
                                                                                                            itemDto)));
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long id) {

        if (!itemRepository.getItemById(id).getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Эта вещь не принадлежит этому пользователю");
        }

        return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(userRepository.getUserById(userId),
                itemDto), id));
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Long userId) {
        userRepository.getUserById(userId);

        return ItemMapper.toItemDtoList(itemRepository.getAllItemsOfUser(userId));
    }

    @Override
    public List<ItemDto> searchByNameAndDescription(Long userId, String text) {
        userRepository.getUserById(userId);

        return ItemMapper.toItemDtoList(itemRepository.searchByNameAndDescription(text));
    }
}
