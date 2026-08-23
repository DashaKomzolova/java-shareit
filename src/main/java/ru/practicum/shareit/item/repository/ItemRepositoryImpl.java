package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new LinkedHashMap<>();
    private Long lastId = 0L;

    @Override
    public Item addItem(Long userId, Item item) {
        item.setOwnerId(userId);
        item.setId(incrementId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, Item updatedItem) {

        if (item.getName() != null && !item.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAllItemsOfUser(Long userId) {
        List<Item> allItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwnerId().equals(userId)) {
                allItems.add(item);
            }
        }

        return allItems;
    }

    @Override
    public List<Item> searchByNameAndDescription(String text) {

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
