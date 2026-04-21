package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.config.UserContext;
import com.animalshelter.analytics.dto.*;
import com.animalshelter.analytics.exception.AccessDeniedException;
import com.animalshelter.analytics.service.FeedingAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/feeding")
public class FeedingAnalyticsController {

    private final FeedingAnalyticsService service;
    private final UserContext userContext;

    public FeedingAnalyticsController(FeedingAnalyticsService service, UserContext userContext) {
        this.service = service;
        this.userContext = userContext;
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<FoodTypeStats>> byType(
            @RequestParam(defaultValue = "30") int days) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getFeedingsByType(days));
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<List<DailySummary>> dailySummary(
            @RequestParam(defaultValue = "30") int days) {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(service.getFeedingDailySummary(days));
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
