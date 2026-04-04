package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.dto.*;
import com.animalshelter.analytics.service.ActivityAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/activities")
public class ActivityAnalyticsController {

    private final ActivityAnalyticsService service;

    public ActivityAnalyticsController(ActivityAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<ActivityTypeStats>> byType(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getActivitiesByType(days));
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<List<DailySummary>> dailySummary(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getActivityDailySummary(days));
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<HeatmapCell>> heatmap(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getActivityHeatmap(days));
    }

    @GetMapping("/top-animals")
    public ResponseEntity<List<TopAnimalActivity>> topAnimals(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getTopAnimals(days, limit));
    }
}
