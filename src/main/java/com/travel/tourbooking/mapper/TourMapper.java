package com.travel.tourbooking.mapper;

import com.travel.tourbooking.dto.TourResponse;
import com.travel.tourbooking.model.Tour;
import java.util.List;
import java.util.stream.Collectors;

public class TourMapper {

    public static TourResponse toResponse(Tour tour) {
        if (tour == null) {
            return null;
        }

        return TourResponse.builder()
                .id(tour.getId())
                .name(tour.getName())
                .description(tour.getDescription())
                .price(tour.getPrice())
                .durationDays(tour.getDurationDays())
                .startDate(tour.getStartDate())
                .maxGroupSize(tour.getMaxGroupSize())
                .availableSlots(tour.getAvailableSlots())
                .status(tour.getStatus())
                .build();
    }

    public static List<TourResponse> toResponseList(List<Tour> tours) {
        if (tours == null) {
            return null;
        }
        return tours.stream()
                .map(TourMapper::toResponse)
                .collect(Collectors.toList());
    }
}
