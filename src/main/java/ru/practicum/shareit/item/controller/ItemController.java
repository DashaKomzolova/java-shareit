package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.request.CommentCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemRequest;
import ru.practicum.shareit.item.dto.response.CommentResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponse addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @Valid @RequestBody ItemCreateRequest itemCreateRequest) {
        return itemService.addItem(userId, itemCreateRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestBody ItemRequest itemRequest,
                                   @PathVariable Long itemId) {
        return itemService.updateItem(userId, itemRequest, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getItemResponseById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponse> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchByNameAndDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam String text) {
        return itemService.searchByNameAndDescription(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId,
                                       @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
        return itemService.addComment(userId, itemId, commentCreateRequest);
    }
}
