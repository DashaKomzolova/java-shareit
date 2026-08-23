package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item addItem(Long userId, Item item);

    Item updateItem(Item item, Item updatedItem);

    Optional<Item> getItemById(Long id);

    List<Item> getAllItemsOfUser(Long userId);

    List<Item> searchByNameAndDescription(String text);
}
