package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody BookingCreateRequest bookingCreateRequest) {
        return bookingService.addBooking(userId, bookingCreateRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponse> getAllBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsOfUser(userId, BookingState.from(state));
    }

    @GetMapping("/owner")
    public List<BookingResponse> getAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsForOwner(userId, BookingState.from(state));
    }
}
