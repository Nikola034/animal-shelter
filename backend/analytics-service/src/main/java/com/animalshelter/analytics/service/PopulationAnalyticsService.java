package com.animalshelter.analytics.service;

import com.animalshelter.analytics.dto.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PopulationAnalyticsService {

    private final MongoTemplate animalRegistryTemplate;

    public PopulationAnalyticsService(
            @Qualifier("animalRegistryTemplate") MongoTemplate animalRegistryTemplate) {
        this.animalRegistryTemplate = animalRegistryTemplate;
    }

    public List<CategoryCount> getAnimalsByCategory() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("category").count().as("count"),
                Aggregation.project("count").and("_id").as("category"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<Document> results =
                animalRegistryTemplate.aggregate(aggregation, "animals", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new CategoryCount(
                        doc.getString("category"),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    public List<StatusCount> getAnimalsByStatus() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("status").count().as("count"),
                Aggregation.project("count").and("_id").as("status"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<Document> results =
                animalRegistryTemplate.aggregate(aggregation, "animals", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new StatusCount(
                        doc.getString("status"),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    public List<GenderCount> getAnimalsByGender() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("gender").count().as("count"),
                Aggregation.project("count").and("_id").as("gender"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<Document> results =
                animalRegistryTemplate.aggregate(aggregation, "animals", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new GenderCount(
                        doc.getString("gender"),
                        doc.get("count", Number.class).longValue()))
                .toList();
    }

    public List<AgeGroupCount> getAgeDistribution() {
        // Use raw $addFields stage with $switch since andExpression() doesn't support raw BSON
        AggregationOperation addAgeRange = context -> Document.parse(
                "{ $addFields: { ageRange: { $switch: { branches: [" +
                "  { case: { $lte: ['$ageMonths', 6] }, then: '0-6 months' }," +
                "  { case: { $lte: ['$ageMonths', 12] }, then: '6-12 months' }," +
                "  { case: { $lte: ['$ageMonths', 24] }, then: '1-2 years' }," +
                "  { case: { $lte: ['$ageMonths', 60] }, then: '2-5 years' }," +
                "  { case: { $lte: ['$ageMonths', 120] }, then: '5-10 years' }" +
                "], default: '10+ years' } } } }"
        );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("ageMonths").ne(null)),
                addAgeRange,
                Aggregation.group("ageRange").count().as("count"),
                Aggregation.project("count").and("_id").as("range")
        );

        AggregationResults<Document> results =
                animalRegistryTemplate.aggregate(aggregation, "animals", Document.class);

        List<AgeGroupCount> list = new ArrayList<>(results.getMappedResults().stream()
                .map(doc -> new AgeGroupCount(
                        doc.getString("range"),
                        doc.get("count", Number.class).longValue()))
                .toList());

        // Sort by predefined order
        List<String> order = List.of("0-6 months", "6-12 months", "1-2 years", "2-5 years", "5-10 years", "10+ years");
        list.sort((a, b) -> {
            int ia = order.indexOf(a.getRange());
            int ib = order.indexOf(b.getRange());
            return Integer.compare(ia < 0 ? 99 : ia, ib < 0 ? 99 : ib);
        });

        return list;
    }

    public PopulationOverview getPopulationOverview() {
        long total = animalRegistryTemplate.getCollection("animals").countDocuments();
        return new PopulationOverview(
                total,
                getAnimalsByCategory(),
                getAnimalsByStatus(),
                getAnimalsByGender(),
                getAgeDistribution()
        );
    }
}
