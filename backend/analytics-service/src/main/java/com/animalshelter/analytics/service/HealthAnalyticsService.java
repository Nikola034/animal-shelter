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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HealthAnalyticsService {

    private final MongoTemplate activityTrackingTemplate;
    private final MongoTemplate animalRegistryTemplate;

    public HealthAnalyticsService(
            @Qualifier("activityTrackingTemplate") MongoTemplate activityTrackingTemplate,
            @Qualifier("animalRegistryTemplate") MongoTemplate animalRegistryTemplate) {
        this.activityTrackingTemplate = activityTrackingTemplate;
        this.animalRegistryTemplate = animalRegistryTemplate;
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
                        formatDate(doc.get("date")),
                        getDoubleValue(doc, "weight_grams")))
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
                        formatDate(doc.get("date")),
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
                        formatDate(doc.get("date")),
                        getDoubleValue(doc, "avgWeight")))
                .toList();
    }

    private Double getDoubleValue(Document doc, String key) {
        Object value = doc.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }

    private String formatDate(Object dateValue) {
        if (dateValue instanceof Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
        }
        return dateValue != null ? dateValue.toString() : "";
    }

    public List<MedicalRecordSummary> getMedicalRecordSummaries(int days) {
        LocalDate since = LocalDate.now().minusDays(days);

        // Query medical records within period
        Query query = new Query(Criteria.where("date").gte(since))
                .with(Sort.by(Sort.Direction.DESC, "date"));

        List<Document> docs = animalRegistryTemplate.find(query, Document.class, "medical_records");

        // Build animal name cache
        Map<String, String> animalNames = new HashMap<>();
        List<Document> animals = animalRegistryTemplate.findAll(Document.class, "animals");
        for (Document animal : animals) {
            String id = animal.getObjectId("_id") != null ? animal.getObjectId("_id").toHexString() : "";
            String name = animal.getString("name");
            if (!id.isEmpty() && name != null) {
                animalNames.put(id, name);
            }
        }

        return docs.stream()
                .map(doc -> {
                    String animalId = doc.getString("animalId");
                    return new MedicalRecordSummary(
                            animalId,
                            animalNames.getOrDefault(animalId, "Unknown"),
                            doc.getString("type"),
                            doc.getString("title"),
                            doc.getString("description"),
                            formatDate(doc.get("date")),
                            doc.getString("veterinarianName"),
                            doc.getString("notes")
                    );
                })
                .toList();
    }
}
