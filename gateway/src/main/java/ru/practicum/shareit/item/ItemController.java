package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemCreateRequest itemCreateRequest) {
        return itemClient.addItem(userId, itemCreateRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemRequest itemRequest,
                                              @PathVariable Long itemId) {
        return itemClient.updateItem(userId, itemId, itemRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByNameAndDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam String text) {
        return itemClient.searchByNameAndDescription(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
        return itemClient.addComment(userId, itemId, commentCreateRequest);
    }
}
