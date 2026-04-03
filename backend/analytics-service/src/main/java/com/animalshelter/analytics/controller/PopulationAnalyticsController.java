package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.dto.*;
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

    public PopulationAnalyticsController(PopulationAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<CategoryCount>> byCategory() {
        return ResponseEntity.ok(service.getAnimalsByCategory());
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<StatusCount>> byStatus() {
        return ResponseEntity.ok(service.getAnimalsByStatus());
    }

    @GetMapping("/by-gender")
    public ResponseEntity<List<GenderCount>> byGender() {
        return ResponseEntity.ok(service.getAnimalsByGender());
    }

    @GetMapping("/age-distribution")
    public ResponseEntity<List<AgeGroupCount>> ageDistribution() {
        return ResponseEntity.ok(service.getAgeDistribution());
    }

    @GetMapping("/overview")
    public ResponseEntity<PopulationOverview> overview() {
        return ResponseEntity.ok(service.getPopulationOverview());
    }
}
