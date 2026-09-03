package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemRequest;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static Item toItem(ItemCreateRequest itemCreateRequest) {
        Item item = new Item();
        item.setName(itemCreateRequest.getName());
        item.setDescription(itemCreateRequest.getDescription());
        item.setAvailable(itemCreateRequest.getAvailable());
        item.setRequestId(itemCreateRequest.getRequestId());
        return item;
    }

    public static Item toItem(ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(itemRequest.getId());
        item.setName(itemRequest.getName());
        item.setDescription(itemRequest.getDescription());
        item.setAvailable(itemRequest.getAvailable());
        item.setOwnerId(itemRequest.getOwnerId());
        item.setRequestId(itemRequest.getRequestId());
        return item;
    }

    public static ItemResponse toItemResponse(Item item) {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(item.getId());
        itemResponse.setName(item.getName());
        itemResponse.setDescription(item.getDescription());
        itemResponse.setAvailable(item.getAvailable());
        itemResponse.setOwnerId(item.getOwnerId());
        itemResponse.setRequestId(item.getRequestId());
        return itemResponse;
    }

    public static List<ItemResponse> toItemResponseList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemResponse)
                .toList();
    }
}
