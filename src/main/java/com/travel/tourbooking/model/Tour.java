package com.travel.tourbooking.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "tours")
@Data
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Integer maxGroupSize;

    @Column(nullable = false)
    private Integer availableSlots;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TourStatus status;

    public enum TourStatus {
        AVAILABLE,
        FULLY_BOOKED,
        CANCELLED
    }

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookingTour> bookings;

    public boolean isActive() {
        return this.status == TourStatus.AVAILABLE;
    }
}
