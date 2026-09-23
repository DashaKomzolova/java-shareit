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
import ru.practicum.shareit.exception.BookingDatesOverlapException;
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

    private static final List<Status> ACTIVE_STATUSES = List.of(Status.WAITING, Status.APPROVED);

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

        boolean hasOverlap = bookingRepository.existsByItem_IdAndStatusInAndStartLessThanAndEndGreaterThan(
                item.getId(), ACTIVE_STATUSES, bookingCreateRequest.getEnd(), bookingCreateRequest.getStart());

        if (hasOverlap) {
            throw new BookingDatesOverlapException(
                    "Вещь уже забронирована на пересекающийся промежуток времени");
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

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBooker_IdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };

        return BookingMapper.toBookingResponseList(bookings);
    }

    @Override
    public List<BookingResponse> getAllBookingsForOwner(Long userId, BookingState state) {
        userService.getUserById(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };

        return BookingMapper.toBookingResponseList(bookings);
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }
}
