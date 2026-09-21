package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.request.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.response.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponse addItemRequest(Long userId, ItemRequestCreateRequest itemRequestCreateRequest);

    List<ItemRequestResponse> getOwnRequests(Long userId);

    List<ItemRequestResponse> getAllRequests(Long userId);

    ItemRequestResponse getRequestById(Long userId, Long requestId);
}
