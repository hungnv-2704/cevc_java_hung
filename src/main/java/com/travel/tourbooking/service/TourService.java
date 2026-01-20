package com.travel.tourbooking.service;

import com.travel.tourbooking.dto.TourRequest;
import com.travel.tourbooking.dto.TourResponse;
import com.travel.tourbooking.mapper.TourMapper;
import com.travel.tourbooking.model.Tour;
import com.travel.tourbooking.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TourService {

    private final TourRepository tourRepository;

    @Autowired
    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TourResponse> findAllResponses() {
        List<Tour> tours = tourRepository.findAll();
        return TourMapper.toResponseList(tours);
    }

    public Optional<Tour> findById(Long id) {
        return tourRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public TourResponse findByIdResponse(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found: " + id));
        return TourMapper.toResponse(tour);
    }

    public Tour create(Tour tour) {
        tour.setId(null);
        return tourRepository.save(tour);
    }

    @Transactional
    public TourResponse createFromRequest(TourRequest request) {
        if (tourRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Tour name already exists: " + request.getName());
        }

        Tour tour = Tour.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .startDate(request.getStartDate())
                .maxGroupSize(request.getMaxGroupSize())
                .availableSlots(request.getMaxGroupSize())
                .status(request.getStatus())
                .build();

        Tour savedTour = tourRepository.save(tour);
        return TourMapper.toResponse(savedTour);
    }

    public Tour update(Tour tour) {
        if (tour.getId() == null || !tourRepository.existsById(tour.getId())) {
            throw new IllegalArgumentException("Tour not found for update: " + tour.getId());
        }
        return tourRepository.save(tour);
    }

    @Transactional
    public TourResponse updateFromRequest(Long id, TourRequest request) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found: " + id));

        if (!tour.getName().equals(request.getName())) {
            if (tourRepository.findByName(request.getName()).isPresent()) {
                throw new IllegalArgumentException("Tour name already exists: " + request.getName());
            }
        }

        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setPrice(request.getPrice());
        tour.setDurationDays(request.getDurationDays());
        tour.setStartDate(request.getStartDate());
        tour.setMaxGroupSize(request.getMaxGroupSize());
        tour.setStatus(request.getStatus());

        Tour updatedTour = tourRepository.save(tour);
        return TourMapper.toResponse(updatedTour);
    }

    public void deleteById(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new IllegalArgumentException("Tour not found for delete: " + id);
        }
        tourRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TourResponse> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllResponses();
        }
        List<Tour> tours = tourRepository.findByNameContainingIgnoreCase(keyword);
        return TourMapper.toResponseList(tours);
    }
}
