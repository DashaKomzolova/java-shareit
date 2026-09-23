package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemIsNotAvailable;
import ru.practicum.shareit.exception.NotOwnerException;

import java.time.LocalDateTime;
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

@WebMvcTest(BookingController.class)
class BookingControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void addBooking_shouldReturn200_whenValid() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponse response = new BookingResponse();
        response.setId(1L);
        response.setStatus(Status.WAITING);

        when(bookingService.addBooking(eq(10L), any(BookingCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void addBooking_shouldReturn400_whenItemNotAvailable() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingService.addBooking(eq(10L), any(BookingCreateRequest.class)))
                .thenThrow(new ItemIsNotAvailable("Эту вещь нельзя забронировать, так как она недоступна"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        BookingResponse response = new BookingResponse();
        response.setId(1L);
        response.setStatus(Status.APPROVED);

        when(bookingService.approveBooking(10L, 1L, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 10L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveBooking_shouldReturn403_whenNotOwner() throws Exception {
        when(bookingService.approveBooking(20L, 1L, true))
                .thenThrow(new NotOwnerException("Подтверждать или отклонять бронирование может только владелец вещи"));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 20L)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        BookingResponse response = new BookingResponse();
        response.setId(1L);

        when(bookingService.getBookingById(10L, 1L)).thenReturn(response);

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllBookingsOfUser_shouldReturnList() throws Exception {
        BookingResponse response = new BookingResponse();
        response.setId(1L);

        when(bookingService.getAllBookingsOfUser(10L, BookingState.ALL)).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 10L).param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllBookingsOfUser_shouldReturn400_whenStateUnknown() throws Exception {
        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 10L).param("state", "NOT_A_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingsForOwner_shouldReturnList() throws Exception {
        BookingResponse response = new BookingResponse();
        response.setId(1L);

        when(bookingService.getAllBookingsForOwner(10L, BookingState.ALL)).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 10L).param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
