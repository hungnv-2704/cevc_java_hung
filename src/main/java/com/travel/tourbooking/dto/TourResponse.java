package com.travel.tourbooking.dto;

import com.travel.tourbooking.model.Tour;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer durationDays;
    private Date startDate;
    private Integer maxGroupSize;
    private Integer availableSlots;
    private Tour.TourStatus status;
}
