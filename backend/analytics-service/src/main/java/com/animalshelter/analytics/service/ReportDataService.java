package com.animalshelter.analytics.service;

import com.animalshelter.analytics.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ReportDataService {

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Belgrade");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Map<String, String> SECTION_LABELS = Map.of(
            "all", "Full",
            "population", "Population",
            "activities", "Activities",
            "feeding", "Feeding",
            "health", "Health"
    );

    private final PopulationAnalyticsService populationService;
    private final ActivityAnalyticsService activityService;
    private final FeedingAnalyticsService feedingService;
    private final HealthAnalyticsService healthService;

    public ReportDataService(PopulationAnalyticsService populationService,
                             ActivityAnalyticsService activityService,
                             FeedingAnalyticsService feedingService,
                             HealthAnalyticsService healthService) {
        this.populationService = populationService;
        this.activityService = activityService;
        this.feedingService = feedingService;
        this.healthService = healthService;
    }

    public ReportData generateReport(String period, String section) {
        int days = "annual".equalsIgnoreCase(period) ? 365 : 30;
        String periodType = days == 365 ? "ANNUAL" : "MONTHLY";
        String sectionNormalized = section != null ? section.toLowerCase() : "all";

        LocalDate periodEnd = LocalDate.now();
        LocalDate periodStart = periodEnd.minusDays(days);

        String sectionLabel = SECTION_LABELS.getOrDefault(sectionNormalized, "Full");
        String title;
        if (periodType.equals("MONTHLY")) {
            title = sectionLabel + " Monthly Report - " + periodEnd.getMonth().name() + " " + periodEnd.getYear();
        } else {
            title = sectionLabel + " Annual Report - " + periodEnd.getYear();
        }

        ReportData data = new ReportData();
        data.setReportTitle(title);
        data.setReportType(periodType);
        data.setSection(sectionNormalized);
        data.setPeriodStart(periodStart);
        data.setPeriodEnd(periodEnd);
        data.setGeneratedAt(java.time.LocalDateTime.now(APP_ZONE).format(FORMATTER));

        boolean includeAll = "all".equals(sectionNormalized);

        // Population
        if (includeAll || "population".equals(sectionNormalized)) {
            data.setPopulationOverview(populationService.getPopulationOverview());
        }

        // Activities
        if (includeAll || "activities".equals(sectionNormalized)) {
            data.setActivityByType(activityService.getActivitiesByType(days));
            data.setActivityDailySummary(activityService.getActivityDailySummary(days));
            data.setTotalActivities(activityService.getTotalActivityCount(days));
            data.setTotalActivityMinutes(activityService.getTotalActivityMinutes(days));
        }

        // Feeding
        if (includeAll || "feeding".equals(sectionNormalized)) {
            data.setFeedingByType(feedingService.getFeedingsByType(days));
            data.setFeedingDailySummary(feedingService.getFeedingDailySummary(days));
            data.setTotalFeedings(feedingService.getTotalFeedingCount(days));
            data.setTotalFoodGrams(feedingService.getTotalFoodGrams(days));
        }

        // Health
        if (includeAll || "health".equals(sectionNormalized)) {
            data.setAverageWeight(healthService.getAverageWeightByCategory(days));
        }

        return data;
    }
}
