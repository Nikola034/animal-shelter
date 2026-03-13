package com.animalshelter.activitytracking.controller;

import com.animalshelter.activitytracking.dto.CreateFeedingRequest;
import com.animalshelter.activitytracking.dto.FeedingRecordResponse;
import com.animalshelter.activitytracking.dto.MessageResponse;
import com.animalshelter.activitytracking.service.ActivityTrackingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/feeding")
public class FeedingController {

    private final ActivityTrackingService service;

    public FeedingController(ActivityTrackingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FeedingRecordResponse> addFeeding(
            @Valid @RequestBody CreateFeedingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addFeeding(request));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<FeedingRecordResponse>> getFeedings(
            @PathVariable String animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getFeedingsByAnimalAndDate(animalId, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteFeeding(@PathVariable String id) {
        service.deleteFeeding(id);
        return ResponseEntity.ok(new MessageResponse(true, "Feeding deleted successfully"));
    }
}
