package com.travel.tourbooking.controller.api;

import com.travel.tourbooking.model.Tour;
import com.travel.tourbooking.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public ResponseEntity<List<Tour>> findAll() {
        return ResponseEntity.ok(tourService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> findById(@PathVariable Long id) {
        Optional<Tour> tour = tourService.findById(id);
        return tour.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tour> create(@RequestBody Tour tour) {
        Tour created = tourService.create(tour);
        return ResponseEntity.created(URI.create("/api/tours/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tour> update(@PathVariable Long id, @RequestBody Tour tour) {
        Optional<Tour> existing = tourService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tour.setId(id);
        Tour updated = tourService.update(tour);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Tour> existing = tourService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tourService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}