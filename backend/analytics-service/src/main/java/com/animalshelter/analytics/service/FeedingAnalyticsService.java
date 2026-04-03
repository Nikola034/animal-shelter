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
public class FeedingAnalyticsService {

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Belgrade");
    private final MongoTemplate activityTrackingTemplate;

    public FeedingAnalyticsService(
            @Qualifier("activityTrackingTemplate") MongoTemplate activityTrackingTemplate) {
        this.activityTrackingTemplate = activityTrackingTemplate;
    }

    public List<FoodTypeStats> getFeedingsByType(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("meal_time").gte(since)),
                Aggregation.group("food_type")
                        .sum("quantity_grams").as("totalGrams")
                        .count().as("count"),
                Aggregation.project("totalGrams", "count").and("_id").as("foodType"),
                Aggregation.sort(Sort.Direction.DESC, "totalGrams")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "feedings", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new FoodTypeStats(
                        doc.getString("foodType"),
                        doc.get("totalGrams", Number.class).doubleValue(),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    public List<DailySummary> getFeedingDailySummary(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        // Raw $addFields stage for $dateToString
        AggregationOperation addDateField = context -> Document.parse(
                "{ $addFields: { date_str: { $dateToString: { format: '%Y-%m-%d', date: '$meal_time', timezone: 'Europe/Belgrade' } } } }"
        );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("meal_time").gte(since)),
                addDateField,
                Aggregation.group("date_str")
                        .sum("quantity_grams").as("totalValue")
                        .count().as("count"),
                Aggregation.project("totalValue", "count").and("_id").as("date"),
                Aggregation.sort(Sort.Direction.ASC, "date")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "feedings", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new DailySummary(
                        doc.getString("date"),
                        doc.get("totalValue", Number.class).longValue(),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    private long countFeedings(Instant since) {
        Criteria criteria = Criteria.where("meal_time").gte(since);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.count().as("total")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "feedings", Document.class);

        Document doc = results.getUniqueMappedResult();
        return doc != null ? doc.get("total", Number.class).longValue() : 0;
    }

    public long getTotalFeedingCount(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();
        return countFeedings(since);
    }

    public double getTotalFoodGrams(int days) {
        Instant since = LocalDate.now().minusDays(days).atStartOfDay(APP_ZONE).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("meal_time").gte(since)),
                Aggregation.group().sum("quantity_grams").as("total")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "feedings", Document.class);

        Document doc = results.getUniqueMappedResult();
        return doc != null ? doc.get("total", Number.class).doubleValue() : 0;
    }
}
