package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemRequest;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemResponse addItem(Long userId, ItemCreateRequest itemCreateRequest) {
        userService.getUserResponseById(userId);

        return ItemMapper.toItemResponse(itemRepository.addItem(userId, ItemMapper.toItem(itemCreateRequest)));
    }

    @Override
    public ItemResponse updateItem(Long userId, ItemRequest itemRequest, Long id) {

        Item updatedItem = getItemById(id);

        if (!updatedItem.getOwnerId().equals(userId)) {
            throw new NotOwnerException("Эта вещь не принадлежит этому пользователю");
        }

        return ItemMapper.toItemResponse(itemRepository.updateItem(ItemMapper.toItem(itemRequest), updatedItem));
    }

    @Override
    public ItemResponse getItemResponseById(Long userId, Long id) {
        return ItemMapper.toItemResponse(itemRepository.getItemById(id)
                .orElseThrow(() -> new NotFoundException("Товар с id " + id + " не найден")));
    }

    @Override
    public List<ItemResponse> getAllItemsOfUser(Long userId) {
        userService.getUserResponseById(userId);

        return ItemMapper.toItemResponseList(itemRepository.getAllItemsOfUser(userId));
    }

    @Override
    public List<ItemResponse> searchByNameAndDescription(Long userId, String text) {
        userService.getUserResponseById(userId);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return ItemMapper.toItemResponseList(itemRepository.searchByNameAndDescription(text));
    }

    private Item getItemById(Long id) {
        return itemRepository.getItemById(id)
                .orElseThrow(() -> new NotFoundException("Товар с id " + id + " не найден"));
    }
}
