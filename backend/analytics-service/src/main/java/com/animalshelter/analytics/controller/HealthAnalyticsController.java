package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.config.UserContext;
import com.animalshelter.analytics.dto.*;
import com.animalshelter.analytics.exception.AccessDeniedException;
import com.animalshelter.analytics.service.HealthAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/health")
public class HealthAnalyticsController {

    private final HealthAnalyticsService service;
    private final UserContext userContext;

    public HealthAnalyticsController(HealthAnalyticsService service, UserContext userContext) {
        this.service = service;
        this.userContext = userContext;
    }

    @GetMapping("/weight-trend")
    public ResponseEntity<WeightTrendResponse> weightTrend(
            @RequestParam String animalId,
            @RequestParam(defaultValue = "30") int days) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getWeightTrend(animalId, days));
    }

    @GetMapping("/energy-mood-trend")
    public ResponseEntity<EnergyMoodTrendResponse> energyMoodTrend(
            @RequestParam String animalId,
            @RequestParam(defaultValue = "30") int days) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getEnergyMoodTrend(animalId, days));
    }

    @GetMapping("/average-weight")
    public ResponseEntity<List<WeightDataPoint>> averageWeight(
            @RequestParam(defaultValue = "30") int days) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getAverageWeightByCategory(days));
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
