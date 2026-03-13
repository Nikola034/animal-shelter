package com.animalshelter.activitytracking.controller;

import com.animalshelter.activitytracking.dto.DailyMeasurementResponse;
import com.animalshelter.activitytracking.dto.MessageResponse;
import com.animalshelter.activitytracking.dto.SaveMeasurementRequest;
import com.animalshelter.activitytracking.service.ActivityTrackingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final ActivityTrackingService service;

    public MeasurementController(ActivityTrackingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DailyMeasurementResponse> saveMeasurement(
            @Valid @RequestBody SaveMeasurementRequest request) {
        return ResponseEntity.ok(service.saveMeasurement(request));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<DailyMeasurementResponse> getMeasurement(
            @PathVariable String animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyMeasurementResponse response = service.getMeasurement(animalId, date);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteMeasurement(@PathVariable String id) {
        service.deleteMeasurement(id);
        return ResponseEntity.ok(new MessageResponse(true, "Measurement deleted successfully"));
    }
}
