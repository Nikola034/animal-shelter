package com.animalshelter.activitytracking.controller;

import com.animalshelter.activitytracking.config.UserContext;
import com.animalshelter.activitytracking.dto.CreateFeedingRequest;
import com.animalshelter.activitytracking.dto.FeedingRecordResponse;
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
@RequestMapping("/api/feeding")
public class FeedingController {

    private final ActivityTrackingService service;
    private final UserContext userContext;

    public FeedingController(ActivityTrackingService service, UserContext userContext) {
        this.service = service;
        this.userContext = userContext;
    }

    @PostMapping
    public ResponseEntity<FeedingRecordResponse> addFeeding(
            @Valid @RequestBody CreateFeedingRequest request) {
        requireRole("Admin", "Caretaker");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addFeeding(request));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<FeedingRecordResponse>> getFeedings(
            @PathVariable String animalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getFeedingsByAnimalAndDate(animalId, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteFeeding(@PathVariable String id) {
        requireRole("Admin", "Caretaker");
        service.deleteFeeding(id);
        return ResponseEntity.ok(new MessageResponse(true, "Feeding deleted successfully"));
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
