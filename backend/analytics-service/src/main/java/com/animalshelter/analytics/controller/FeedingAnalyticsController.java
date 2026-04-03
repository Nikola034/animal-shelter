package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.dto.*;
import com.animalshelter.analytics.service.FeedingAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/feeding")
public class FeedingAnalyticsController {

    private final FeedingAnalyticsService service;

    public FeedingAnalyticsController(FeedingAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<FoodTypeStats>> byType(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getFeedingsByType(days));
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<List<DailySummary>> dailySummary(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getFeedingDailySummary(days));
    }
}
