package com.travel.tourbooking.repository;

import com.travel.tourbooking.model.BookingTour;
import com.travel.tourbooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingTourRepository extends JpaRepository<BookingTour, Long> {

    Optional<BookingTour> findByIdAndUserId(Long id, Long userId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BookingTour b WHERE b.id = :id")
    Optional<BookingTour> findByIdForUpdate(Long id);
    
    List<BookingTour> findByTourId(Long tourId);
    List<BookingTour> findByUserId(Long userId);
    
    List<BookingTour> findAllByUserIdOrderByBookedAtDesc(Long userId);
    List<BookingTour> findAllByTourIdOrderByBookedAtDesc(Long tourId);

    List<BookingTour> findByStatus(BookingStatus status);
    List<BookingTour> findByStatusAndUserId(BookingStatus status, Long userId);
    List<BookingTour> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByTourIdAndUserId(Long tourId, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);

    long countByTourId(Long tourId);
    long countByStatus(BookingStatus status);
}