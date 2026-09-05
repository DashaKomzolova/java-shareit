package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(Long itemId,
                                                                                Status status,
                                                                                LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId,
                                                                              Status status,
                                                                              LocalDateTime now);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId,
                                                              Long bookerId,
                                                              Status status,
                                                              LocalDateTime end);
}
