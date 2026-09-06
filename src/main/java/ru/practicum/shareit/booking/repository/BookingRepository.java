package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                            LocalDateTime start,
                                                                            LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByItem_IdInAndStatusOrderByStartAsc(List<Long> itemIds, Status status);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId,
                                                              Long bookerId,
                                                              Status status,
                                                              LocalDateTime end);

    boolean existsByItem_IdAndStatusInAndStartLessThanAndEndGreaterThan(Long itemId,
                                                                         List<Status> statuses,
                                                                         LocalDateTime end,
                                                                         LocalDateTime start);
}
