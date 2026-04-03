package com.animalshelter.analytics.service;

import com.animalshelter.analytics.dto.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final MongoTemplate animalRegistryTemplate;
    private final PopulationAnalyticsService populationService;
    private final ActivityAnalyticsService activityService;
    private final FeedingAnalyticsService feedingService;

    public DashboardService(
            @Qualifier("animalRegistryTemplate") MongoTemplate animalRegistryTemplate,
            PopulationAnalyticsService populationService,
            ActivityAnalyticsService activityService,
            FeedingAnalyticsService feedingService) {
        this.animalRegistryTemplate = animalRegistryTemplate;
        this.populationService = populationService;
        this.activityService = activityService;
        this.feedingService = feedingService;
    }

    public DashboardResponse getDashboard() {
        long totalAnimals = animalRegistryTemplate.getCollection("animals").countDocuments();

        long activeAnimals = animalRegistryTemplate.count(
                new Query(Criteria.where("status").is("Active")), "animals");
        long adoptedAnimals = animalRegistryTemplate.count(
                new Query(Criteria.where("status").is("Adopted")), "animals");

        int days = 30;

        long totalActivities = activityService.getTotalActivityCount(days);
        long totalActivityMinutes = activityService.getTotalActivityMinutes(days);

        long totalFeedings = feedingService.getTotalFeedingCount(days);
        double totalFoodGrams = feedingService.getTotalFoodGrams(days);

        List<CategoryCount> animalsByCategory = populationService.getAnimalsByCategory();

        List<ActivityTypeStats> topActivityTypes = activityService.getActivitiesByType(days);
        List<FoodTypeStats> topFoodTypes = feedingService.getFeedingsByType(days);

        return new DashboardResponse(
                totalAnimals,
                activeAnimals,
                adoptedAnimals,
                totalActivities,
                totalActivityMinutes,
                totalFeedings,
                totalFoodGrams,
                animalsByCategory,
                topActivityTypes,
                topFoodTypes
        );
    }
}
