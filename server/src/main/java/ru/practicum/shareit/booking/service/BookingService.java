package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse addBooking(Long userId, BookingCreateRequest bookingCreateRequest);

    BookingResponse approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponse getBookingById(Long userId, Long bookingId);

    List<BookingResponse> getAllBookingsOfUser(Long userId, BookingState state);

    List<BookingResponse> getAllBookingsForOwner(Long userId, BookingState state);
}
