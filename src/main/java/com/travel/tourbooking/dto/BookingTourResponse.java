package com.travel.tourbooking.dto;

import lombok.*;
import com.travel.tourbooking.enums.BookingStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingTourResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String tourId;
    private String tourName;
    private String userId;

    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;

    private int numberOfGuests;
    private BigDecimal totalPrice;
    private String currency;

    private LocalDate startDate;
    private LocalDate endDate;

    private Instant createdAt;
    private Instant updatedAt;

    public enum PaymentStatus {
        UNPAID,
        PAID,
        REFUNDED,
        FAILED
    }
}