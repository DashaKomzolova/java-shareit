package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingAlreadyProcessedException;
import ru.practicum.shareit.exception.DatesException;
import ru.practicum.shareit.exception.ItemIsABookerItemException;
import ru.practicum.shareit.exception.ItemIsNotAvailable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingResponse addBooking(Long userId, BookingCreateRequest bookingCreateRequest) {
        if (!bookingCreateRequest.getStart().isBefore(bookingCreateRequest.getEnd())) {
            throw new DatesException("Дата начала бронирования должна быть раньше даты его окончания");
        }

        User booker = userService.getUserById(userId);
        Item item = itemService.getItemById(userId, bookingCreateRequest.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new ItemIsABookerItemException("Владелец не может забронировать свою же вещь");
        }

        if (!item.getAvailable()) {
            throw new ItemIsNotAvailable("Эту вещь нельзя забронировать, так как она недоступна");
        }

        Booking booking = BookingMapper.toBooking(bookingCreateRequest);
        booking.setBooker(booker);
        booking.setItem(item);

        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Подтверждать или отклонять бронирование может только владелец вещи");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new BookingAlreadyProcessedException("Решение по этому бронированию уже принято");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new NotOwnerException("Просматривать это бронирование может только автор бронирования или владелец вещи");
        }

        return BookingMapper.toBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getAllBookingsOfUser(Long userId, BookingState state) {
        userService.getUserById(userId);

        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);

        return BookingMapper.toBookingResponseList(filterByState(bookings, state));
    }

    @Override
    public List<BookingResponse> getAllBookingsForOwner(Long userId, BookingState state) {
        userService.getUserById(userId);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId);

        return BookingMapper.toBookingResponseList(filterByState(bookings, state));
    }

    private List<Booking> filterByState(List<Booking> bookings, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> bookings.stream()
                    .filter(b -> !b.getStart().isAfter(now) && b.getEnd().isAfter(now))
                    .toList();
            case PAST -> bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .toList();
            case FUTURE -> bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .toList();
            case WAITING -> bookings.stream()
                    .filter(b -> b.getStatus() == Status.WAITING)
                    .toList();
            case REJECTED -> bookings.stream()
                    .filter(b -> b.getStatus() == Status.REJECTED)
                    .toList();
            case ALL -> bookings;
        };
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }
}
