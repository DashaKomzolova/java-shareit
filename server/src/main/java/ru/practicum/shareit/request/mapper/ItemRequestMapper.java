package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.request.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.response.ItemRequestItemDto;
import ru.practicum.shareit.request.dto.response.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestCreateRequest itemRequestCreateRequest) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestCreateRequest.getDescription());
        return itemRequest;
    }

    public static ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest, List<Item> items) {
        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(itemRequest.getId());
        response.setDescription(itemRequest.getDescription());
        response.setCreated(itemRequest.getCreated());
        response.setItems(items.stream()
                .map(item -> new ItemRequestItemDto(item.getId(), item.getName(), item.getOwner().getId()))
                .toList());
        return response;
    }
}
