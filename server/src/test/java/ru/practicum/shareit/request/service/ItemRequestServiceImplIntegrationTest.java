package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.request.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.response.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
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
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requestor;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Requestor");
        requestor.setEmail("requestor_" + System.nanoTime() + "@mail.com");
        requestor = userRepository.save(requestor);

        otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other_" + System.nanoTime() + "@mail.com");
        otherUser = userRepository.save(otherUser);
    }

    @Test
    void addItemRequest_shouldSaveWithCreatedTimestampAndEmptyItems() {
        ItemRequestCreateRequest createRequest = new ItemRequestCreateRequest();
        createRequest.setDescription("Need a drill");

        ItemRequestResponse response = itemRequestService.addItemRequest(requestor.getId(), createRequest);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getDescription()).isEqualTo("Need a drill");
        assertThat(response.getCreated()).isNotNull();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void addItemRequest_shouldThrowNotFoundException_whenUserNotExists() {
        ItemRequestCreateRequest createRequest = new ItemRequestCreateRequest();
        createRequest.setDescription("Need a drill");

        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(999999L, createRequest));
    }

    @Test
    void getOwnRequests_shouldReturnRequestorsOwnRequestsOrderedByCreatedDesc() {
        ItemRequest first = saveRequest(requestor, "First request", LocalDateTime.now().minusHours(2));
        ItemRequest second = saveRequest(requestor, "Second request", LocalDateTime.now().minusHours(1));
        saveRequest(otherUser, "Someone else's request", LocalDateTime.now());

        List<ItemRequestResponse> ownRequests = itemRequestService.getOwnRequests(requestor.getId());

        assertThat(ownRequests).hasSize(2);
        assertThat(ownRequests.get(0).getId()).isEqualTo(second.getId());
        assertThat(ownRequests.get(1).getId()).isEqualTo(first.getId());
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests_excludingOwn() {
        saveRequest(requestor, "My own request", LocalDateTime.now());
        ItemRequest otherRequest = saveRequest(otherUser, "Someone else's request", LocalDateTime.now());

        List<ItemRequestResponse> allRequests = itemRequestService.getAllRequests(requestor.getId());

        assertThat(allRequests).extracting(ItemRequestResponse::getId).containsExactly(otherRequest.getId());
    }

    @Test
    void getRequestById_shouldReturnRequestWithLinkedItems() {
        ItemRequest request = saveRequest(otherUser, "Need a drill", LocalDateTime.now());

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(requestor);
        item.setRequest(request);
        itemRepository.save(item);

        ItemRequestResponse response = itemRequestService.getRequestById(requestor.getId(), request.getId());

        assertThat(response.getId()).isEqualTo(request.getId());
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void getRequestById_shouldThrowNotFoundException_whenNotExists() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(requestor.getId(), 999999L));
    }

    private ItemRequest saveRequest(User owner, String description, LocalDateTime created) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(owner);
        request.setCreated(created);
        return itemRequestRepository.save(request);
    }
}
