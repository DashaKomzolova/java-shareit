package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.request.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.response.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toItemRequest_shouldMapDescriptionFromRequest() {
        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("Нужна дрель");

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(request);

        assertThat(itemRequest.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void toItemRequestResponse_shouldMapFieldsAndItemsList() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setCreated(LocalDateTime.of(2026, 1, 1, 12, 0));

        User owner = new User();
        owner.setId(5L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");
        item.setOwner(owner);

        ItemRequestResponse response = ItemRequestMapper.toItemRequestResponse(itemRequest, List.of(item));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDescription()).isEqualTo("Нужна дрель");
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getId()).isEqualTo(10L);
        assertThat(response.getItems().get(0).getName()).isEqualTo("Дрель");
        assertThat(response.getItems().get(0).getOwnerId()).isEqualTo(5L);
    }

    @Test
    void toItemRequestResponse_shouldReturnEmptyItemsList_whenNoItemsProvided() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(2L);
        itemRequest.setDescription("Нужна пила");
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestResponse response = ItemRequestMapper.toItemRequestResponse(itemRequest, List.of());

        assertThat(response.getItems()).isEmpty();
    }
}
