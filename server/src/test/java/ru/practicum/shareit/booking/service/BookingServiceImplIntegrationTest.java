package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingAlreadyProcessedException;
import ru.practicum.shareit.exception.BookingDatesOverlapException;
import ru.practicum.shareit.exception.DatesException;
import ru.practicum.shareit.exception.ItemIsABookerItemException;
import ru.practicum.shareit.exception.ItemIsNotAvailable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @PersistenceContext
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private Item availableItem;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_" + System.nanoTime() + "@mail.com");
        owner = persist(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker_" + System.nanoTime() + "@mail.com");
        booker = persist(booker);

        availableItem = new Item();
        availableItem.setName("Drill");
        availableItem.setDescription("Powerful drill");
        availableItem.setAvailable(true);
        availableItem.setOwner(owner);
        availableItem = persist(availableItem);
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    private BookingCreateRequest makeRequest(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setItemId(itemId);
        request.setStart(start);
        request.setEnd(end);
        return request;
    }

    @Test
    void addBooking_shouldCreateBooking_whenValid() {
        BookingResponse response = bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.WAITING);
        assertThat(response.getItem().getId()).isEqualTo(availableItem.getId());
        assertThat(response.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void addBooking_shouldThrowDatesException_whenStartAfterEnd() {
        assertThrows(DatesException.class, () -> bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1))));
    }

    @Test
    void addBooking_shouldThrowItemIsABookerItemException_whenOwnerBooksOwnItem() {
        assertThrows(ItemIsABookerItemException.class, () -> bookingService.addBooking(owner.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
    }

    @Test
    void addBooking_shouldThrowItemIsNotAvailable_whenItemNotAvailable() {
        availableItem.setAvailable(false);
        entityManager.flush();

        assertThrows(ItemIsNotAvailable.class, () -> bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
    }

    @Test
    void addBooking_shouldThrowBookingDatesOverlapException_whenOverlapping() {
        bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3)));

        assertThrows(BookingDatesOverlapException.class, () -> bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4))));
    }

    @Test
    void approveBooking_shouldSetStatusApproved() {
        BookingResponse created = bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingResponse approved = bookingService.approveBooking(owner.getId(), created.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void approveBooking_shouldSetStatusRejected_whenApprovedFalse() {
        BookingResponse created = bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingResponse rejected = bookingService.approveBooking(owner.getId(), created.getId(), false);

        assertThat(rejected.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void approveBooking_shouldThrowNotOwnerException_whenNotOwner() {
        BookingResponse created = bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThrows(NotOwnerException.class, () -> bookingService.approveBooking(booker.getId(), created.getId(), true));
    }

    @Test
    void approveBooking_shouldThrowBookingAlreadyProcessedException_whenAlreadyProcessed() {
        BookingResponse created = bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        bookingService.approveBooking(owner.getId(), created.getId(), true);

        assertThrows(BookingAlreadyProcessedException.class,
                () -> bookingService.approveBooking(owner.getId(), created.getId(), false));
    }

    @Test
    void getBookingById_shouldReturnBooking_forBookerOrOwner() {
        BookingResponse created = bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingResponse asBooker = bookingService.getBookingById(booker.getId(), created.getId());
        BookingResponse asOwner = bookingService.getBookingById(owner.getId(), created.getId());

        assertThat(asBooker.getId()).isEqualTo(created.getId());
        assertThat(asOwner.getId()).isEqualTo(created.getId());
    }

    @Test
    void getBookingById_shouldThrowNotOwnerException_forStranger() {
        BookingResponse created = bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        User stranger = new User();
        stranger.setName("Stranger");
        stranger.setEmail("stranger_" + System.nanoTime() + "@mail.com");
        stranger = persist(stranger);

        Long strangerId = stranger.getId();
        assertThrows(NotOwnerException.class, () -> bookingService.getBookingById(strangerId, created.getId()));
    }

    @Test
    void getBookingById_shouldThrowNotFoundException_whenNotExists() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booker.getId(), 999999L));
    }

    @Test
    void getAllBookingsOfUser_shouldFilterByWaitingState() {
        bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        List<BookingResponse> waiting = bookingService.getAllBookingsOfUser(booker.getId(), BookingState.WAITING);
        List<BookingResponse> all = bookingService.getAllBookingsOfUser(booker.getId(), BookingState.ALL);

        assertThat(waiting).hasSize(1);
        assertThat(all).hasSize(1);
    }

    @Test
    void getAllBookingsForOwner_shouldReturnOwnersBookings() {
        bookingService.addBooking(booker.getId(),
                makeRequest(availableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        List<BookingResponse> ownerBookings = bookingService.getAllBookingsForOwner(owner.getId(), BookingState.ALL);

        assertThat(ownerBookings).hasSize(1);
    }
}
