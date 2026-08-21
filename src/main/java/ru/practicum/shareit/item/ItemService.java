package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long id);

    ItemDto getItemById(Long userId, Long id);

    List<ItemDto> getAllItemsOfUser(Long userId);

    List<ItemDto> searchByNameAndDescription(Long userId, String text);
}
