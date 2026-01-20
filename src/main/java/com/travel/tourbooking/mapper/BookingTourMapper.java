package com.travel.tourbooking.mapper;

import com.travel.tourbooking.dto.BookingTourResponse;
import com.travel.tourbooking.model.BookingTour;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class BookingTourMapper {

    public static BookingTourResponse toResponse(BookingTour booking) {
        if (booking == null) {
            return null;
        }

        return BookingTourResponse.builder()
                .id(booking.getId() != null ? booking.getId().toString() : null)
                .tourId(booking.getTour() != null ? booking.getTour().getId().toString() : null)
                .tourName(booking.getTour() != null ? booking.getTour().getName() : null)
                .userId(booking.getUserId() != null ? booking.getUserId().toString() : 
                        (booking.getUser() != null ? booking.getUser().getId().toString() : null))
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(mapPaymentStatus(booking.getPaymentStatus()))
                .numberOfGuests(booking.getNumberOfPeople())
                .totalPrice(booking.getTotalPrice())
                .currency("VND")
                .startDate(booking.getBookingDate())
                .endDate(booking.getBookingDate() != null && booking.getTour() != null 
                    ? booking.getBookingDate().plusDays(booking.getTour().getDurationDays()) 
                    : null)
                .createdAt(booking.getCreatedAt() != null 
                    ? booking.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant() 
                    : null)
                .updatedAt(booking.getUpdatedAt() != null 
                    ? booking.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant() 
                    : null)
                .build();
    }

    public static List<BookingTourResponse> toResponseList(List<BookingTour> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .map(BookingTourMapper::toResponse)
                .collect(Collectors.toList());
    }

    private static BookingTourResponse.PaymentStatus mapPaymentStatus(BookingTour.PaymentStatus status) {
        if (status == null) {
            return null;
        }
        return BookingTourResponse.PaymentStatus.valueOf(status.name());
    }
}
