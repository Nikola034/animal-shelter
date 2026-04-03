package com.animalshelter.analytics.service;

import com.animalshelter.analytics.dto.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ActivityAnalyticsService {

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Belgrade");
    private final MongoTemplate activityTrackingTemplate;

    public ActivityAnalyticsService(
            @Qualifier("activityTrackingTemplate") MongoTemplate activityTrackingTemplate) {
        this.activityTrackingTemplate = activityTrackingTemplate;
    }

    public List<ActivityTypeStats> getActivitiesByType(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("recorded_at").gte(since)),
                Aggregation.group("activity_type")
                        .sum("duration_minutes").as("totalMinutes")
                        .count().as("count"),
                Aggregation.project("totalMinutes", "count").and("_id").as("activityType"),
                Aggregation.sort(Sort.Direction.DESC, "totalMinutes")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "activities", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new ActivityTypeStats(
                        doc.getString("activityType"),
                        doc.get("totalMinutes", Number.class).longValue(),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    public List<DailySummary> getActivityDailySummary(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        // Raw $addFields stage for $dateToString
        AggregationOperation addDateField = context -> Document.parse(
                "{ $addFields: { date_str: { $dateToString: { format: '%Y-%m-%d', date: '$recorded_at', timezone: 'Europe/Belgrade' } } } }"
        );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("recorded_at").gte(since)),
                addDateField,
                Aggregation.group("date_str")
                        .sum("duration_minutes").as("totalValue")
                        .count().as("count"),
                Aggregation.project("totalValue", "count").and("_id").as("date"),
                Aggregation.sort(Sort.Direction.ASC, "date")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "activities", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new DailySummary(
                        doc.getString("date"),
                        doc.get("totalValue", Number.class).longValue(),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    public List<HeatmapCell> getActivityHeatmap(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        // Raw $addFields stage for $dayOfWeek and $hour
        AggregationOperation addTimeFields = context -> Document.parse(
                "{ $addFields: { " +
                "  dow: { $dayOfWeek: { date: '$recorded_at', timezone: 'Europe/Belgrade' } }, " +
                "  hr: { $hour: { date: '$recorded_at', timezone: 'Europe/Belgrade' } } " +
                "} }"
        );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("recorded_at").gte(since)),
                addTimeFields,
                Aggregation.group("dow", "hr").count().as("count"),
                Aggregation.project("count")
                        .and("_id.dow").as("dayOfWeek")
                        .and("_id.hr").as("hour"),
                Aggregation.sort(Sort.Direction.ASC, "dayOfWeek", "hour")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "activities", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new HeatmapCell(
                        doc.get("dayOfWeek", Number.class).intValue(),
                        doc.get("hour", Number.class).intValue(),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    public List<TopAnimalActivity> getTopAnimals(int days, int limit) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("recorded_at").gte(since)),
                Aggregation.group("animal_id")
                        .sum("duration_minutes").as("totalMinutes")
                        .count().as("activityCount"),
                Aggregation.project("totalMinutes", "activityCount").and("_id").as("animalId"),
                Aggregation.sort(Sort.Direction.DESC, "totalMinutes"),
                Aggregation.limit(limit)
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "activities", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new TopAnimalActivity(
                        doc.getString("animalId"),
                        doc.get("totalMinutes", Number.class).longValue(),
                        doc.get("activityCount", Number.class).longValue()))
                .toList();
    }

    public long getTotalActivityCount(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("recorded_at").gte(since)),
                Aggregation.count().as("total")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "activities", Document.class);

        Document doc = results.getUniqueMappedResult();
        return doc != null ? doc.get("total", Number.class).longValue() : 0;
    }

    public long getTotalActivityMinutes(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("recorded_at").gte(since)),
                Aggregation.group().sum("duration_minutes").as("total")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "activities", Document.class);

        Document doc = results.getUniqueMappedResult();
        return doc != null ? doc.get("total", Number.class).longValue() : 0;
    }
}
