package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.request.CommentCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemRequest;
import ru.practicum.shareit.item.dto.response.CommentResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemResponse addItem(Long userId, ItemCreateRequest itemCreateRequest);

    ItemResponse updateItem(Long userId, ItemRequest itemRequest, Long id);

    ItemResponse getItemResponseById(Long userId, Long id);

    List<ItemResponse> getAllItemsOfUser(Long userId);

    List<ItemResponse> searchByNameAndDescription(Long userId, String text);

    Item getItemById(Long userId, Long id);

    CommentResponse addComment(Long userId, Long itemId, CommentCreateRequest commentCreateRequest);
}
