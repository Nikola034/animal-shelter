package com.animalshelter.animalregistry.service;

import com.animalshelter.animalregistry.model.Animal;
import com.animalshelter.animalregistry.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that manages vector embeddings for animals.
 * Generates embeddings via text2vec-transformers and stores them
 * directly in MongoDB animal documents for Atlas Vector Search.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnimalVectorService {

    private final EmbeddingService embeddingService;
    private final AnimalRepository animalRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Build a rich composite text from all animal fields for optimal semantic search.
     */
    public String buildCompositeText(Animal animal) {
        StringBuilder sb = new StringBuilder();
        sb.append(animal.getName() != null ? animal.getName() : "Unknown");
        sb.append(" is a ");

        if (animal.getGender() != null) {
            sb.append(animal.getGender().name().toLowerCase()).append(" ");
        }
        if (animal.getCategory() != null) {
            sb.append(animal.getCategory().name().toLowerCase());
        }

        if (animal.getBreed() != null && !animal.getBreed().isBlank()) {
            sb.append(", breed: ").append(animal.getBreed());
        }

        if (animal.getAgeMonths() != null) {
            int years = animal.getAgeMonths() / 12;
            int months = animal.getAgeMonths() % 12;
            sb.append(", age: ");
            if (years > 0) {
                sb.append(years).append(years == 1 ? " year" : " years");
                if (months > 0) sb.append(" and ").append(months).append(months == 1 ? " month" : " months");
            } else {
                sb.append(months).append(months == 1 ? " month" : " months");
            }
        }

        if (animal.getColor() != null && !animal.getColor().isBlank()) {
            sb.append(", color: ").append(animal.getColor());
        }

        if (animal.getWeight() != null) {
            sb.append(", weight: ").append(String.format("%.1f", animal.getWeight())).append(" kg");
        }

        if (animal.getStatus() != null) {
            sb.append(". Status: ").append(animal.getStatus().name());
        }

        if (animal.getDescription() != null && !animal.getDescription().isBlank()) {
            sb.append(". ").append(animal.getDescription());
        }

        return sb.toString();
    }

    /**
     * Generate embedding for an animal and store it directly in MongoDB.
     */
    public void upsertAnimal(Animal animal) {
        try {
            String compositeText = buildCompositeText(animal);
            List<Double> embedding = embeddingService.getEmbedding(compositeText);

            animal.setEmbedding(embedding);
            animalRepository.save(animal);

            log.info("========== Animal '{}' (ID: {}) embedding stored in MongoDB ({} dimensions) ==========",
                    animal.getName(), animal.getId(), embedding.size());
        } catch (Exception e) {
            log.error("========== Error generating embedding for animal '{}': {} ==========",
                    animal.getName(), e.getMessage(), e);
        }
    }

    /**
     * Clear embedding when animal is deleted (handled by normal delete, but kept for explicit cleanup).
     */
    public void deleteAnimal(String mongoId) {
        log.info("========== Animal (ID: {}) deleted — embedding removed with document ==========", mongoId);
    }

    /**
     * Perform semantic search using MongoDB Atlas Vector Search ($vectorSearch).
     * Returns animals with their vectorSearchScore.
     */
    public List<AnimalSearchResult> semanticSearch(String query, int limit) {
        try {
            // Step 1: Generate embedding for the query text
            List<Double> queryVector = embeddingService.getEmbedding(query);

            // Step 2: Run $vectorSearch aggregation pipeline
            int numCandidates = Math.max(limit * 10, 50);

            Document vectorSearchStage = new Document("$vectorSearch",
                    new Document("index", "animal_vector_index")
                            .append("path", "embedding")
                            .append("queryVector", queryVector)
                            .append("numCandidates", numCandidates)
                            .append("limit", limit)
            );

            Document projectStage = new Document("$project",
                    new Document("_id", 1)
                            .append("name", 1)
                            .append("category", 1)
                            .append("breed", 1)
                            .append("gender", 1)
                            .append("status", 1)
                            .append("color", 1)
                            .append("ageMonths", 1)
                            .append("description", 1)
                            .append("score", new Document("$meta", "vectorSearchScore"))
            );

            List<Document> pipeline = List.of(vectorSearchStage, projectStage);

            List<Document> results = mongoTemplate.getDb()
                    .getCollection("animals")
                    .aggregate(pipeline)
                    .into(new ArrayList<>());

            List<AnimalSearchResult> searchResults = results.stream()
                    .map(doc -> new AnimalSearchResult(
                            doc.getObjectId("_id").toString(),
                            doc.getDouble("score")
                    ))
                    .collect(Collectors.toList());

            log.info("========== MongoDB vector search for '{}' returned {} results ==========",
                    query, searchResults.size());
            return searchResults;

        } catch (Exception e) {
            log.error("========== Error during MongoDB vector search: {} ==========", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Sync all animals: generate embeddings and store in MongoDB.
     */
    public int syncAll(List<Animal> animals) {
        int successCount = 0;
        for (Animal animal : animals) {
            try {
                upsertAnimal(animal);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to sync animal '{}': {}", animal.getName(), e.getMessage());
            }
        }
        log.info("========== Synced {}/{} animals with embeddings in MongoDB ==========", successCount, animals.size());
        return successCount;
    }

    /**
     * Simple result holder for vector search results.
     */
    public record AnimalSearchResult(String animalId, double score) {}
}
