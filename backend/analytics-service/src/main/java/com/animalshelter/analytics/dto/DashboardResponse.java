package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalAnimals;
    private long activeAnimals;
    private long adoptedAnimals;

    private long totalActivitiesLast30Days;
    private long totalActivityMinutesLast30Days;

    private long totalFeedingsLast30Days;
    private double totalFoodGramsLast30Days;

    private List<CategoryCount> animalsByCategory;
    private List<ActivityTypeStats> topActivityTypes;
    private List<FoodTypeStats> topFoodTypes;
}
