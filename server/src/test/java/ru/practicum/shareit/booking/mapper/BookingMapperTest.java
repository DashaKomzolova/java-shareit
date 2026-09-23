package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBooking_shouldMapRequestFieldsAndSetWaitingStatus() {
        BookingCreateRequest request = new BookingCreateRequest();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        request.setStart(start);
        request.setEnd(end);
        request.setItemId(10L);

        Booking booking = BookingMapper.toBooking(request);

        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void toBookingResponse_shouldMapAllFieldsIncludingNestedItemAndBooker() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        BookingResponse response = BookingMapper.toBookingResponse(booking);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(response.getEnd()).isEqualTo(LocalDateTime.of(2026, 1, 2, 10, 0));
        assertThat(response.getItem().getId()).isEqualTo(1L);
        assertThat(response.getItem().getName()).isEqualTo("Дрель");
        assertThat(response.getBooker().getId()).isEqualTo(2L);
        assertThat(response.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void toBookingResponseList_shouldMapEachBookingInList() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        User booker = new User();
        booker.setId(2L);

        Booking first = new Booking();
        first.setId(1L);
        first.setItem(item);
        first.setBooker(booker);
        first.setStatus(Status.WAITING);

        Booking second = new Booking();
        second.setId(2L);
        second.setItem(item);
        second.setBooker(booker);
        second.setStatus(Status.REJECTED);

        List<BookingResponse> result = BookingMapper.toBookingResponseList(List.of(first, second));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }
}
