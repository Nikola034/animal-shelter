package com.animalshelter.activitytracking.controller;

import com.animalshelter.activitytracking.config.UserContext;
import com.animalshelter.activitytracking.dto.DailyMeasurementResponse;
import com.animalshelter.activitytracking.dto.MessageResponse;
import com.animalshelter.activitytracking.dto.SaveMeasurementRequest;
import com.animalshelter.activitytracking.exception.AccessDeniedException;
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
    private final UserContext userContext;

    public MeasurementController(ActivityTrackingService service, UserContext userContext) {
        this.service = service;
        this.userContext = userContext;
    }

    @PostMapping
    public ResponseEntity<DailyMeasurementResponse> saveMeasurement(
            @Valid @RequestBody SaveMeasurementRequest request) {
        requireRole("Admin", "Caretaker");
        return ResponseEntity.ok(service.saveMeasurement(request));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<DailyMeasurementResponse> getMeasurement(
            @PathVariable String animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        DailyMeasurementResponse response = service.getMeasurement(animalId, date);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteMeasurement(@PathVariable String id) {
        requireRole("Admin", "Caretaker");
        service.deleteMeasurement(id);
        return ResponseEntity.ok(new MessageResponse(true, "Measurement deleted successfully"));
    }

    private void requireRole(String... allowedRoles) {
        String currentRole = userContext.getRole();
        for (String role : allowedRoles) {
            if (role.equals(currentRole)) {
                return;
            }
        }
        throw new AccessDeniedException("Access denied. Required role: " + String.join(" or ", allowedRoles));
    }
}
