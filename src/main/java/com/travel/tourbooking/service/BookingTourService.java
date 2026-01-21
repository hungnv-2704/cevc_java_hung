package com.travel.tourbooking.service;

import com.travel.tourbooking.model.BookingTour;
import com.travel.tourbooking.model.BookingTour.PaymentStatus;
import com.travel.tourbooking.enums.BookingStatus;
import com.travel.tourbooking.model.Tour;
import com.travel.tourbooking.model.User;
import com.travel.tourbooking.repository.BookingTourRepository;
import com.travel.tourbooking.repository.TourRepository;
import com.travel.tourbooking.repository.UserRepository;
import com.travel.tourbooking.dto.BookingTourRequest;
import com.travel.tourbooking.dto.BookingTourResponse;
import com.travel.tourbooking.mapper.BookingTourMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingTourService {

    private final BookingTourRepository bookingTourRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public BookingTourService(
            BookingTourRepository bookingTourRepository,
            TourRepository tourRepository,
            UserRepository userRepository
    ) {
        this.bookingTourRepository = bookingTourRepository;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingTour bookTour(Long userId, Long tourId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Tour tour = tourRepository.findByIdForUpdate(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found: " + tourId));

        if (!tour.isActive()) {
            throw new IllegalStateException("Tour is not active");
        }

        if (tour.getAvailableSlots() < quantity) {
            throw new IllegalStateException("Not enough available slots");
        }

        tour.setAvailableSlots(tour.getAvailableSlots() - quantity);
        tourRepository.save(tour);

        BookingTour booking = new BookingTour();
        booking.setUser(user);
        booking.setTour(tour);
        booking.setQuantity(quantity);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(LocalDateTime.now());

        return bookingTourRepository.save(booking);
    }

    @Transactional
    public BookingTour cancelBooking(Long bookingId) {
        BookingTour booking = bookingTourRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be canceled");
        }

        Tour tour = tourRepository.findByIdForUpdate(booking.getTour().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tour not found: " + booking.getTour().getId()));

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCanceledAt(LocalDateTime.now());
        bookingTourRepository.save(booking);

        tour.setAvailableSlots(tour.getAvailableSlots() + booking.getQuantity());
        tourRepository.save(tour);

        return booking;
    }

    @Transactional
    public BookingTour updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        BookingTour booking = bookingTourRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.setStatus(newStatus);
        
        if (newStatus == BookingStatus.CANCELLED) {
            booking.setCanceledAt(LocalDateTime.now());
            
            Tour tour = tourRepository.findByIdForUpdate(booking.getTour().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Tour not found: " + booking.getTour().getId()));
            tour.setAvailableSlots(tour.getAvailableSlots() + booking.getQuantity());
            tourRepository.save(tour);
        }

        return bookingTourRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public BookingTour getBooking(Long bookingId) {
        return bookingTourRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }

    @Transactional(readOnly = true)
    public List<BookingTour> listUserBookings(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return bookingTourRepository.findAllByUserIdOrderByBookedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<BookingTour> listTourBookings(Long tourId) {
        tourRepository.findById(tourId);
        return bookingTourRepository.findAllByTourIdOrderByBookedAtDesc(tourId);
    }

    public List<BookingTourResponse> findAll() {
        List<BookingTour> bookings = bookingTourRepository.findAll();
        return BookingTourMapper.toResponseList(bookings);
    }

    public BookingTourResponse findById(Long id) {
        BookingTour booking = bookingTourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
        return BookingTourMapper.toResponse(booking);
    }

    @Transactional
    public BookingTourResponse create(BookingTourRequest request, Long userId) {
        if (request.getTourId() == null) {
            throw new IllegalArgumentException("Tour ID is required");
        }
        if (request.getAdults() == null || request.getAdults() <= 0) {
            throw new IllegalArgumentException("Number of adults must be greater than zero");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Tour tour = tourRepository.findByIdForUpdate(request.getTourId())
                .orElseThrow(() -> new IllegalArgumentException("Tour not found: " + request.getTourId()));

        if (!tour.isActive()) {
            throw new IllegalStateException("Tour is not active");
        }

        int totalPeople = request.getAdults() + (request.getChildren() != null ? request.getChildren() : 0);
        
        if (tour.getAvailableSlots() < totalPeople) {
            throw new IllegalStateException("Not enough available slots");
        }

        tour.setAvailableSlots(tour.getAvailableSlots() - totalPeople);
        tourRepository.save(tour);

        LocalDateTime now = LocalDateTime.now();
        BookingTour booking = BookingTour.builder()
                .user(user)
                .tour(tour)
                .quantity(1)
                .bookingDate(request.getStartDate() != null ? request.getStartDate() : now.toLocalDate())
                .numberOfPeople(totalPeople)
                .totalPrice(tour.getPrice() != null 
                    ? java.math.BigDecimal.valueOf(tour.getPrice() * totalPeople) 
                    : java.math.BigDecimal.ZERO)
                .bookingStatus(BookingStatus.PENDING)
                .paymentStatus(BookingTour.PaymentStatus.UNPAID)
                .notes(request.getSpecialRequests())
                .bookedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        BookingTour savedBooking = bookingTourRepository.save(booking);
        return BookingTourMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingTourResponse update(Long id, BookingTourRequest request) {
        BookingTour booking = bookingTourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));

        if (request.getStartDate() != null) {
            booking.setBookingDate(request.getStartDate());
        }
        
        if (request.getAdults() != null && request.getAdults() > 0) {
            int newTotalPeople = request.getAdults() + (request.getChildren() != null ? request.getChildren() : 0);
            int oldPeople = booking.getNumberOfPeople();
            int difference = newTotalPeople - oldPeople;
            
            if (difference != 0) {
                Tour tour = booking.getTour();
                if (difference > 0) {
                    if (tour.getAvailableSlots() < difference) {
                        throw new IllegalStateException("Not enough available slots for the update");
                    }
                    tour.setAvailableSlots(tour.getAvailableSlots() - difference);
                } else {
                    tour.setAvailableSlots(tour.getAvailableSlots() - difference); // difference is negative
                }
                tourRepository.save(tour);
                
                booking.setNumberOfPeople(newTotalPeople);
                if (tour.getPrice() != null) {
                    booking.setTotalPrice(java.math.BigDecimal.valueOf(tour.getPrice() * newTotalPeople));
                }
            }
        }
        
        if (request.getSpecialRequests() != null) {
            booking.setNotes(request.getSpecialRequests());
        }
        
        booking.setUpdatedAt(LocalDateTime.now());
        BookingTour updatedBooking = bookingTourRepository.save(booking);
        return BookingTourMapper.toResponse(updatedBooking);
    }

    @Transactional
    public boolean delete(Long id) {
        BookingTour booking = bookingTourRepository.findById(id).orElse(null);
        if (booking == null) {
            return false;
        }

        Tour tour = booking.getTour();
        if (tour != null) {
            tour.setAvailableSlots(tour.getAvailableSlots() + booking.getNumberOfPeople());
            tourRepository.save(tour);
        }

        bookingTourRepository.delete(booking);
        return true;
    }

    @Transactional
    public BookingTour updatePaymentStatus(Long bookingId, PaymentStatus newPaymentStatus) {
        BookingTour booking = bookingTourRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.setPaymentStatus(newPaymentStatus);
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingTourRepository.save(booking);
    }
}
