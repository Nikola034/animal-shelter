package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.config.UserContext;
import com.animalshelter.analytics.dto.*;
import com.animalshelter.analytics.exception.AccessDeniedException;
import com.animalshelter.analytics.service.PopulationAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/population")
public class PopulationAnalyticsController {

    private final PopulationAnalyticsService service;
    private final UserContext userContext;

    public PopulationAnalyticsController(PopulationAnalyticsService service, UserContext userContext) {
        this.service = service;
        this.userContext = userContext;
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<CategoryCount>> byCategory() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getAnimalsByCategory());
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<StatusCount>> byStatus() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getAnimalsByStatus());
    }

    @GetMapping("/by-gender")
    public ResponseEntity<List<GenderCount>> byGender() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getAnimalsByGender());
    }

    @GetMapping("/age-distribution")
    public ResponseEntity<List<AgeGroupCount>> ageDistribution() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getAgeDistribution());
    }

    @GetMapping("/overview")
    public ResponseEntity<PopulationOverview> overview() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getPopulationOverview());
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
