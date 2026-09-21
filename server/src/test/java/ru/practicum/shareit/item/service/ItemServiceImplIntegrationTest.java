package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.request.CommentCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemRequest;
import ru.practicum.shareit.item.dto.response.CommentResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_" + System.nanoTime() + "@mail.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker_" + System.nanoTime() + "@mail.com");
        booker = userRepository.save(booker);
    }

    private ItemCreateRequest makeCreateRequest(String name, String description, Boolean available, Long requestId) {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName(name);
        request.setDescription(description);
        request.setAvailable(available);
        request.setRequestId(requestId);
        return request;
    }

    @Test
    void addItem_shouldSaveItemWithOwner() {
        ItemResponse response = itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Powerful drill", true, null));

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Drill");
        assertThat(response.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void addItem_shouldLinkToRequest_whenRequestIdProvided() {
        ru.practicum.shareit.request.model.ItemRequest itemRequest = new ru.practicum.shareit.request.model.ItemRequest();
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequestor(booker);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemResponse response = itemService.addItem(owner.getId(),
                makeCreateRequest("Drill", "Powerful drill", true, itemRequest.getId()));

        assertThat(response.getRequestId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void addItem_shouldThrowNotFoundException_whenRequestIdInvalid() {
        assertThrows(NotFoundException.class,
                () -> itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Powerful drill", true, 999999L)));
    }

    @Test
    void updateItem_shouldUpdateFields_whenOwner() {
        ItemResponse created = itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Old description", true, null));

        ItemRequest updateRequest = new ItemRequest();
        updateRequest.setName("Updated drill");
        updateRequest.setAvailable(false);

        ItemResponse updated = itemService.updateItem(owner.getId(), updateRequest, created.getId());

        assertThat(updated.getName()).isEqualTo("Updated drill");
        assertThat(updated.getAvailable()).isFalse();
        assertThat(updated.getDescription()).isEqualTo("Old description");
    }

    @Test
    void updateItem_shouldThrowNotOwnerException_whenNotOwner() {
        ItemResponse created = itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Old description", true, null));

        ItemRequest updateRequest = new ItemRequest();
        updateRequest.setName("Hacked name");

        assertThrows(NotOwnerException.class,
                () -> itemService.updateItem(booker.getId(), updateRequest, created.getId()));
    }

    @Test
    void getItemResponseById_shouldReturnItemWithComments() {
        ItemResponse created = itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Description", true, null));

        ItemResponse found = itemService.getItemResponseById(owner.getId(), created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getComments()).isNotNull();
    }

    @Test
    void getItemResponseById_shouldThrowNotFoundException_whenNotExists() {
        assertThrows(NotFoundException.class, () -> itemService.getItemResponseById(owner.getId(), 999999L));
    }

    @Test
    void getAllItemsOfUser_shouldReturnItemsWithBookingsAndComments() {
        ItemResponse created = itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Description", true, null));

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setBooker(booker);
        pastBooking.setItem(fetchItemEntity(created.getId()));
        bookingRepository.save(pastBooking);

        List<ItemResponse> items = itemService.getAllItemsOfUser(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getLastBooking()).isNotNull();
    }

    @Test
    void searchByNameAndDescription_shouldReturnMatchingAvailableItems() {
        itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Powerful drill", true, null));
        itemService.addItem(owner.getId(), makeCreateRequest("Hammer", "Heavy hammer", false, null));

        List<ItemResponse> found = itemService.searchByNameAndDescription(booker.getId(), "drill");

        assertThat(found).extracting(ItemResponse::getName).contains("Drill");
    }

    @Test
    void searchByNameAndDescription_shouldReturnEmptyList_whenTextBlank() {
        List<ItemResponse> found = itemService.searchByNameAndDescription(booker.getId(), "");

        assertThat(found).isEmpty();
    }

    @Test
    void addComment_shouldSaveComment_whenUserHasCompletedBooking() {
        ItemResponse created = itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Description", true, null));

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setBooker(booker);
        pastBooking.setItem(fetchItemEntity(created.getId()));
        bookingRepository.save(pastBooking);

        CommentCreateRequest commentRequest = new CommentCreateRequest();
        commentRequest.setText("Great tool!");

        CommentResponse response = itemService.addComment(booker.getId(), created.getId(), commentRequest);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getText()).isEqualTo("Great tool!");
        assertThat(response.getAuthorName()).isEqualTo("Booker");
    }

    @Test
    void addComment_shouldThrowCommentNotAllowedException_whenUserHasNoCompletedBooking() {
        ItemResponse created = itemService.addItem(owner.getId(), makeCreateRequest("Drill", "Description", true, null));

        CommentCreateRequest commentRequest = new CommentCreateRequest();
        commentRequest.setText("Great tool!");

        assertThrows(CommentNotAllowedException.class,
                () -> itemService.addComment(booker.getId(), created.getId(), commentRequest));
    }

    private Item fetchItemEntity(Long itemId) {
        return itemService.getItemById(owner.getId(), itemId);
    }
}
