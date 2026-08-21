package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new LinkedHashMap<>();
    private Long lastId = 0L;

    @Override
    public Item addItem(Item item) {
        item.setId(incrementId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, Long id) {
        Item foundItem = getItemById(id);

        if (item.getName() != null && !item.getName().isBlank()) {
            foundItem.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            foundItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            foundItem.setAvailable(item.getAvailable());
        }

        return foundItem;
    }

    @Override
    public Item getItemById(Long id) {
        Item foundItem = items.get(id);

        if (foundItem == null) {
            throw new NotFoundException("Такого товара не существует");
        }
        return foundItem;
    }

    @Override
    public List<Item> getAllItemsOfUser(Long userId) {
        List<Item> allItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                allItems.add(item);
            }
        }

        return allItems;
    }

    @Override
    public List<Item> searchByNameAndDescription(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<Item> allMatching = new ArrayList<>();
        String textLower = text.toLowerCase();

        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(textLower)
                    || item.getDescription().toLowerCase().contains(textLower))
                    && item.getAvailable()) {
                allMatching.add(item);
            }
        }

        return allMatching;
    }

    private Long incrementId() {
        return ++lastId;
    }

}
