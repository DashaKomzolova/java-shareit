package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.request.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.response.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void addItemRequest_shouldReturn200_whenValid() throws Exception {
        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("Need a drill");

        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(1L);
        response.setDescription("Need a drill");
        response.setCreated(LocalDateTime.now());
        response.setItems(List.of());

        when(itemRequestService.addItemRequest(eq(10L), any(ItemRequestCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void addItemRequest_shouldReturn400_whenDescriptionBlank() throws Exception {
        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnRequests_shouldReturnList() throws Exception {
        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(1L);
        response.setDescription("Need a drill");
        response.setItems(List.of());

        when(itemRequestService.getOwnRequests(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(2L);
        response.setDescription("Someone else's request");
        response.setItems(List.of());

        when(itemRequestService.getAllRequests(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(1L);
        response.setDescription("Need a drill");
        response.setItems(List.of());

        when(itemRequestService.getRequestById(10L, 1L)).thenReturn(response);

        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getRequestById_shouldReturn404_whenNotExists() throws Exception {
        when(itemRequestService.getRequestById(10L, 999L))
                .thenThrow(new NotFoundException("Запрос с id 999 не найден"));

        mockMvc.perform(get("/requests/999").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isNotFound());
    }
}
