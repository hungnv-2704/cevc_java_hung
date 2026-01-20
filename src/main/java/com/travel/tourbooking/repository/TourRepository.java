package com.travel.tourbooking.repository;

import com.travel.tourbooking.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    Optional<Tour> findById(Number id);
    
    Optional<Tour> findByName(String name);
    
    List<Tour> findByNameContainingIgnoreCase(String keyword);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tour t WHERE t.id = :id")
    Optional<Tour> findByIdForUpdate(Long id);

    List<Tour> findByStatus(String status);

    Page<Tour> findByStartDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Tour> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
}