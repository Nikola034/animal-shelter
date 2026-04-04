package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {
    private String reportTitle;
    private String reportType; // "MONTHLY" or "ANNUAL"
    private String section; // "all", "population", "activities", "feeding", "health"
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String generatedAt;

    // Population data
    private PopulationOverview populationOverview;

    // Activity data
    private List<ActivityTypeStats> activityByType;
    private List<DailySummary> activityDailySummary;
    private long totalActivities;
    private long totalActivityMinutes;

    // Feeding data
    private List<FoodTypeStats> feedingByType;
    private List<DailySummary> feedingDailySummary;
    private long totalFeedings;
    private double totalFoodGrams;

    // Health data
    private List<WeightDataPoint> averageWeight;
}
