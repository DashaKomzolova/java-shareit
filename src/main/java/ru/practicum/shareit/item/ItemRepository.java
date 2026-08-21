package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item, Long id);

    Item getItemById(Long id);

    List<Item> getAllItemsOfUser(Long userId);

    List<Item> searchByNameAndDescription(String text);
}
