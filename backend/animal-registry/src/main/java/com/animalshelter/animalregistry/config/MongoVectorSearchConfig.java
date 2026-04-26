package com.animalshelter.animalregistry.config;

import com.animalshelter.animalregistry.model.Animal;
import com.animalshelter.animalregistry.repository.AnimalRepository;
import com.animalshelter.animalregistry.service.AnimalVectorService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class MongoVectorSearchConfig {

    private static final int EMBEDDING_DIMENSIONS = 384;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Order(10)
    public ApplicationRunner vectorSearchIndexInitializer(MongoTemplate mongoTemplate) {
        return args -> createVectorSearchIndex(mongoTemplate);
    }

    @Bean
    @Order(20)
    public ApplicationRunner animalEmbeddingBackfillRunner(
            AnimalRepository animalRepository,
            AnimalVectorService animalVectorService
    ) {
        return args -> backfillMissingEmbeddings(animalRepository, animalVectorService);
    }

    private void createVectorSearchIndex(MongoTemplate mongoTemplate) {
        String indexName = "animal_vector_index";
        String collectionName = "animals";

        for (int attempt = 1; attempt <= 10; attempt++) {
            try {
                // Check if index already exists
                Document listIndexes = new Document("listSearchIndexes", collectionName);
                Document result = mongoTemplate.getDb().runCommand(listIndexes);
                Document cursor = result.get("cursor", Document.class);
                List<Document> existingIndexes = cursor.getList("firstBatch", Document.class);

                boolean indexExists = existingIndexes.stream()
                        .anyMatch(idx -> indexName.equals(idx.getString("name")));

                if (indexExists) {
                    log.info("========== MongoDB vector search index '{}' already exists ==========", indexName);
                    return;
                }

                // Create the vector search index
                Document indexDefinition = new Document("fields", List.of(
                        new Document("type", "vector")
                                .append("path", "embedding")
                                .append("numDimensions", EMBEDDING_DIMENSIONS)
                                .append("similarity", "cosine"),
                        new Document("type", "filter")
                                .append("path", "category"),
                        new Document("type", "filter")
                                .append("path", "status")
                ));

                Document createCommand = new Document("createSearchIndexes", collectionName)
                        .append("indexes", List.of(
                                new Document("name", indexName)
                                        .append("type", "vectorSearch")
                                        .append("definition", indexDefinition)
                        ));

                mongoTemplate.getDb().runCommand(createCommand);
                log.info("========== MongoDB vector search index '{}' created successfully ==========", indexName);
                return;

            } catch (Exception e) {
                log.warn("Vector search index init attempt {}/10 failed: {}", attempt, e.getMessage());
                if (attempt < 10) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error("========== Failed to create MongoDB vector search index after 10 attempts ==========");
    }

    /**
     * On every startup, find animals that are missing an embedding (or have a wrong-sized one,
     * e.g. from an older model) and ask the embedding service to fill them in. Animals that
     * already have a valid 384-dim embedding are skipped, so this is a cheap no-op once the
     * collection is fully embedded.
     *
     * Retries the unembedded subset a few times so a slow text2vec-transformers container
     * doesn't permanently leave seed animals unsearchable.
     */
    private void backfillMissingEmbeddings(
            AnimalRepository animalRepository,
            AnimalVectorService animalVectorService
    ) {
        final int maxAttempts = 10;
        final long delayMs = 3000;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            List<Animal> missing = animalRepository.findAll().stream()
                    .filter(this::needsEmbedding)
                    .collect(Collectors.toList());

            if (missing.isEmpty()) {
                log.info("========== All animals already have valid embeddings — backfill skipped ==========");
                return;
            }

            log.info("========== Embedding backfill attempt {}/{}: {} animal(s) missing embeddings ==========",
                    attempt, maxAttempts, missing.size());

            int synced = animalVectorService.syncAll(missing);

            if (synced == missing.size()) {
                log.info("========== Embedding backfill complete: {}/{} animals synced ==========",
                        synced, missing.size());
                return;
            }

            log.warn("========== Embedding backfill attempt {}/{} synced {}/{} — retrying ==========",
                    attempt, maxAttempts, synced, missing.size());

            if (attempt < maxAttempts) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        log.error("========== Embedding backfill failed after {} attempts ==========", maxAttempts);
    }

    private boolean needsEmbedding(Animal animal) {
        return animal.getEmbedding() == null || animal.getEmbedding().size() != EMBEDDING_DIMENSIONS;
    }
}
