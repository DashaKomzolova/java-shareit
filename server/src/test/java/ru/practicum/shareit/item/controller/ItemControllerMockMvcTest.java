package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.request.CommentCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemRequest;
import ru.practicum.shareit.item.dto.response.CommentResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem_shouldReturn200_whenValid() throws Exception {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Drill");
        request.setDescription("Powerful drill");
        request.setAvailable(true);

        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("Drill");
        response.setOwnerId(10L);

        when(itemService.addItem(eq(10L), any(ItemCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void addItem_shouldReturn400_whenNameBlank() throws Exception {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("");
        request.setDescription("Powerful drill");
        request.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setName("Updated drill");

        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("Updated drill");

        when(itemService.updateItem(eq(10L), any(ItemRequest.class), eq(1L))).thenReturn(response);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated drill"));
    }

    @Test
    void updateItem_shouldReturn403_whenNotOwner() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setName("Hacked");

        when(itemService.updateItem(eq(20L), any(ItemRequest.class), eq(1L)))
                .thenThrow(new NotOwnerException("Эта вещь не принадлежит этому пользователю"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("Drill");

        when(itemService.getItemResponseById(10L, 1L)).thenReturn(response);

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllItemsOfUser_shouldReturnList() throws Exception {
        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("Drill");

        when(itemService.getAllItemsOfUser(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchByNameAndDescription_shouldReturnMatchingItems() throws Exception {
        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("Drill");

        when(itemService.searchByNameAndDescription(10L, "drill")).thenReturn(List.of(response));

        mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", 10L).param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addComment_shouldReturn200_whenValid() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText("Great tool!");

        CommentResponse response = new CommentResponse();
        response.setId(1L);
        response.setText("Great tool!");
        response.setAuthorName("Booker");

        when(itemService.addComment(eq(20L), eq(1L), any(CommentCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great tool!"));
    }

    @Test
    void addComment_shouldReturn400_whenTextBlank() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_shouldReturn400_whenCommentNotAllowed() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText("Great tool!");

        when(itemService.addComment(eq(20L), eq(1L), any(CommentCreateRequest.class)))
                .thenThrow(new CommentNotAllowedException("Оставить отзыв может только пользователь, который брал эту вещь в аренду"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
