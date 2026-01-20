package com.travel.tourbooking.controller.api;

import com.travel.tourbooking.service.BookingTourService;
import com.travel.tourbooking.dto.BookingTourRequest;
import com.travel.tourbooking.dto.BookingTourResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingTourController {

    private final BookingTourService bookingTourService;

    @Autowired
    public BookingTourController(BookingTourService bookingTourService) {
        this.bookingTourService = bookingTourService;
    }

    @GetMapping
    public ResponseEntity<List<BookingTourResponse>> findAll() {
        return ResponseEntity.ok(bookingTourService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingTourResponse> getById(@PathVariable Long id) {
        BookingTourResponse res = bookingTourService.findById(id);
        return res != null ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<BookingTourResponse> create(@RequestBody BookingTourRequest request) {
        BookingTourResponse created = bookingTourService.create(request, null);
        return ResponseEntity.created(URI.create("/api/bookings/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingTourResponse> update(@PathVariable Long id, @RequestBody BookingTourRequest request) {
        BookingTourResponse updated = bookingTourService.update(id, request);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = bookingTourService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}