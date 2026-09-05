package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.request.CommentCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemRequest;
import ru.practicum.shareit.item.dto.response.CommentResponse;
import ru.practicum.shareit.item.dto.response.ItemBookingDto;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponse addItem(Long userId, ItemCreateRequest itemCreateRequest) {

        Item item = ItemMapper.toItem(itemCreateRequest);
        item.setOwner(userService.getUserById(userId));

        return ItemMapper.toItemResponse(itemRepository.save(item));
    }

    @Override
    public ItemResponse updateItem(Long userId, ItemRequest itemRequest, Long id) {

        Item updatedItem = getItemById(id);

        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Эта вещь не принадлежит этому пользователю");
        }

        if (itemRequest.getName() != null && !itemRequest.getName().isBlank()) {
            updatedItem.setName(itemRequest.getName());
        }

        if (itemRequest.getDescription() != null && !itemRequest.getDescription().isBlank()) {
            updatedItem.setDescription(itemRequest.getDescription());
        }

        if (itemRequest.getAvailable() != null) {
            updatedItem.setAvailable(itemRequest.getAvailable());
        }

        return ItemMapper.toItemResponse(itemRepository.save(updatedItem));
    }

    @Override
    public ItemResponse getItemResponseById(Long userId, Long id) {
        Item item = getItemById(id);

        ItemResponse response = ItemMapper.toItemResponse(item);
        response.setComments(CommentMapper.toCommentResponseList(
                commentRepository.findByItem_IdOrderByCreatedDesc(id)));

        return response;
    }

    @Override
    public List<ItemResponse> getAllItemsOfUser(Long userId) {
        userService.getUserResponseById(userId);

        List<Item> items = itemRepository.findByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    ItemResponse response = ItemMapper.toItemResponse(item);
                    enrichWithBookings(response, item.getId(), now);
                    response.setComments(CommentMapper.toCommentResponseList(
                            commentRepository.findByItem_IdOrderByCreatedDesc(item.getId())));
                    return response;
                })
                .toList();
    }

    @Override
    public List<ItemResponse> searchByNameAndDescription(Long userId, String text) {
        userService.getUserResponseById(userId);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return ItemMapper.toItemResponseList(itemRepository.searchByNameAndDescription(text));
    }

    @Override
    public Item getItemById(Long userId, Long id) {
        userService.getUserResponseById(userId);

        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар с id " + id + " не найден"));
    }

    @Override
    public CommentResponse addComment(Long userId, Long itemId, CommentCreateRequest commentCreateRequest) {
        User author = userService.getUserById(userId);
        Item item = getItemById(itemId);

        boolean hasRented = bookingRepository.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now());

        if (!hasRented) {
            throw new CommentNotAllowedException(
                    "Оставить отзыв может только пользователь, который брал эту вещь в аренду, "
                            + "и только после окончания срока аренды");
        }

        Comment comment = CommentMapper.toComment(commentCreateRequest);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentResponse(commentRepository.save(comment));
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар с id " + id + " не найден"));
    }

    private void enrichWithBookings(ItemResponse response, Long itemId, LocalDateTime now) {
        bookingRepository.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, now)
                .ifPresent(b -> response.setLastBooking(new ItemBookingDto(b.getId(), b.getBooker().getId())));

        bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, now)
                .ifPresent(b -> response.setNextBooking(new ItemBookingDto(b.getId(), b.getBooker().getId())));
    }
}
