package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingBookerDto;
import ru.practicum.shareit.booking.dto.response.BookingItemDto;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public class BookingMapper {

    public static Booking toBooking(BookingCreateRequest bookingCreateRequest) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateRequest.getStart());
        booking.setEnd(bookingCreateRequest.getEnd());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingResponse toBookingResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getId());
        bookingResponse.setStart(booking.getStart());
        bookingResponse.setEnd(booking.getEnd());
        bookingResponse.setItem(new BookingItemDto(booking.getItem().getId(), booking.getItem().getName()));
        bookingResponse.setBooker(new BookingBookerDto(booking.getBooker().getId()));
        bookingResponse.setStatus(booking.getStatus());
        return bookingResponse;
    }

    public static List<BookingResponse> toBookingResponseList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingResponse)
                .toList();
    }
}
