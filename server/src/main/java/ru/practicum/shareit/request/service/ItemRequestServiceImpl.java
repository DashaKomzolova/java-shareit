package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.request.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.response.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemRequestResponse addItemRequest(Long userId, ItemRequestCreateRequest itemRequestCreateRequest) {
        User requestor = userService.getUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestCreateRequest);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestResponse(saved, List.of());
    }

    @Override
    public List<ItemRequestResponse> getOwnRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(userId);

        return buildResponses(requests);
    }

    @Override
    public List<ItemRequestResponse> getAllRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId);

        return buildResponses(requests);
    }

    @Override
    public ItemRequestResponse getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<Item> items = itemRepository.findByRequest_IdIn(List.of(requestId));

        return ItemRequestMapper.toItemRequestResponse(itemRequest, items);
    }

    private List<ItemRequestResponse> buildResponses(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();

        Map<Long, List<Item>> itemsByRequest = itemRepository.findByRequest_IdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestResponse(
                        request, itemsByRequest.getOrDefault(request.getId(), List.of())))
                .toList();
    }
}
