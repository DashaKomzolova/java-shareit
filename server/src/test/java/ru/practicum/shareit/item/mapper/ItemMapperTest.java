package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItem_fromCreateRequest_shouldMapNameDescriptionAvailable() {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Дрель");
        request.setDescription("Простая дрель");
        request.setAvailable(true);

        Item item = ItemMapper.toItem(request);

        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Простая дрель");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void toItem_fromUpdateRequest_shouldMapIdNameDescriptionAvailable() {
        ru.practicum.shareit.item.dto.request.ItemRequest request =
                new ru.practicum.shareit.item.dto.request.ItemRequest();
        request.setId(5L);
        request.setName("Отвертка");
        request.setDescription("Крестовая");
        request.setAvailable(false);

        Item item = ItemMapper.toItem(request);

        assertThat(item.getId()).isEqualTo(5L);
        assertThat(item.getName()).isEqualTo("Отвертка");
        assertThat(item.getDescription()).isEqualTo("Крестовая");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void toItemResponse_shouldMapOwnerIdAndRequestId_whenBothPresent() {
        User owner = new User();
        owner.setId(1L);
        ru.practicum.shareit.request.model.ItemRequest itemRequest =
                new ru.practicum.shareit.request.model.ItemRequest();
        itemRequest.setId(7L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);

        ItemResponse response = ItemMapper.toItemResponse(item);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getOwnerId()).isEqualTo(1L);
        assertThat(response.getRequestId()).isEqualTo(7L);
    }

    @Test
    void toItemResponse_shouldReturnNullOwnerAndRequestId_whenBothAbsent() {
        Item item = new Item();
        item.setId(11L);
        item.setName("Молоток");
        item.setDescription("Описание");
        item.setAvailable(true);

        ItemResponse response = ItemMapper.toItemResponse(item);

        assertThat(response.getOwnerId()).isNull();
        assertThat(response.getRequestId()).isNull();
    }

    @Test
    void toItemResponseList_shouldMapEachItemInList() {
        Item first = new Item();
        first.setId(1L);
        Item second = new Item();
        second.setId(2L);

        List<ItemResponse> result = ItemMapper.toItemResponseList(List.of(first, second));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }
}
