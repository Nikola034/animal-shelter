package com.animalshelter.analytics.service;

import com.animalshelter.analytics.dto.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HealthAnalyticsService {

    private final MongoTemplate activityTrackingTemplate;

    public HealthAnalyticsService(
            @Qualifier("activityTrackingTemplate") MongoTemplate activityTrackingTemplate) {
        this.activityTrackingTemplate = activityTrackingTemplate;
    }

    public WeightTrendResponse getWeightTrend(String animalId, int days) {
        LocalDate since = LocalDate.now().minusDays(days);

        Query query = new Query(Criteria.where("animal_id").is(animalId)
                .and("date").gte(since)
                .and("weight_grams").ne(null))
                .with(Sort.by(Sort.Direction.ASC, "date"));

        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "daily_measurements");

        List<WeightDataPoint> data = docs.stream()
                .map(doc -> new WeightDataPoint(
                        doc.get("date").toString(),
                        doc.getDouble("weight_grams")))
                .toList();

        return new WeightTrendResponse(animalId, data);
    }

    public EnergyMoodTrendResponse getEnergyMoodTrend(String animalId, int days) {
        LocalDate since = LocalDate.now().minusDays(days);

        Query query = new Query(Criteria.where("animal_id").is(animalId)
                .and("date").gte(since))
                .with(Sort.by(Sort.Direction.ASC, "date"));

        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "daily_measurements");

        List<EnergyMoodDataPoint> data = docs.stream()
                .map(doc -> new EnergyMoodDataPoint(
                        doc.get("date").toString(),
                        doc.getInteger("energy_level"),
                        doc.getInteger("mood_level")))
                .toList();

        return new EnergyMoodTrendResponse(animalId, data);
    }

    public List<WeightDataPoint> getAverageWeightByCategory(int days) {
        // This joins animal_registry and activity_tracking via animal_id
        // Since we can't do cross-database joins in MongoDB, we return avg weight per day across all animals
        LocalDate since = LocalDate.now().minusDays(days);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("date").gte(since)
                        .and("weight_grams").ne(null)),
                Aggregation.group("date")
                        .avg("weight_grams").as("avgWeight")
                        .count().as("count"),
                Aggregation.project("avgWeight").and("_id").as("date"),
                Aggregation.sort(Sort.Direction.ASC, "date")
        );

        AggregationResults<Document> results =
                activityTrackingTemplate.aggregate(aggregation, "daily_measurements", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new WeightDataPoint(
                        doc.get("date").toString(),
                        doc.getDouble("avgWeight")))
                .toList();
    }
}
