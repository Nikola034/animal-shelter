package com.animalshelter.activitytracking.controller;

import com.animalshelter.activitytracking.dto.ActivityRecordResponse;
import com.animalshelter.activitytracking.dto.CreateActivityRequest;
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
@RequestMapping("/api/activities")
public class ActivityRecordController {

    private final ActivityTrackingService service;

    public ActivityRecordController(ActivityTrackingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ActivityRecordResponse> addActivity(
            @Valid @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addActivity(request));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<ActivityRecordResponse>> getActivities(
            @PathVariable String animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getActivitiesByAnimalAndDate(animalId, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteActivity(@PathVariable String id) {
        service.deleteActivity(id);
        return ResponseEntity.ok(new MessageResponse(true, "Activity deleted successfully"));
    }
}
