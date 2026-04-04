package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.dto.*;
import com.animalshelter.analytics.service.HealthAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/health")
public class HealthAnalyticsController {

    private final HealthAnalyticsService service;

    public HealthAnalyticsController(HealthAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/weight-trend")
    public ResponseEntity<WeightTrendResponse> weightTrend(
            @RequestParam String animalId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getWeightTrend(animalId, days));
    }

    @GetMapping("/energy-mood-trend")
    public ResponseEntity<EnergyMoodTrendResponse> energyMoodTrend(
            @RequestParam String animalId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getEnergyMoodTrend(animalId, days));
    }

    @GetMapping("/average-weight")
    public ResponseEntity<List<WeightDataPoint>> averageWeight(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getAverageWeightByCategory(days));
    }
}
