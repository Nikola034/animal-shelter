package com.animalshelter.activitytracking.controller;

import com.animalshelter.activitytracking.config.UserContext;
import com.animalshelter.activitytracking.dto.ActivityRecordResponse;
import com.animalshelter.activitytracking.dto.CreateActivityRequest;
import com.animalshelter.activitytracking.dto.MessageResponse;
import com.animalshelter.activitytracking.exception.AccessDeniedException;
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
    private final UserContext userContext;

    public ActivityRecordController(ActivityTrackingService service, UserContext userContext) {
        this.service = service;
        this.userContext = userContext;
    }

    @PostMapping
    public ResponseEntity<ActivityRecordResponse> addActivity(
            @Valid @RequestBody CreateActivityRequest request) {
        requireRole("Admin", "Caretaker");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addActivity(request));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<ActivityRecordResponse>> getActivities(
            @PathVariable String animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getActivitiesByAnimalAndDate(animalId, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteActivity(@PathVariable String id) {
        requireRole("Admin", "Caretaker");
        service.deleteActivity(id);
        return ResponseEntity.ok(new MessageResponse(true, "Activity deleted successfully"));
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
