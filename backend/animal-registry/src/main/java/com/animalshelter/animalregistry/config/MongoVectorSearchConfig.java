package com.animalshelter.animalregistry.config;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@Slf4j
public class MongoVectorSearchConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ApplicationRunner vectorSearchIndexInitializer(MongoTemplate mongoTemplate) {
        return args -> createVectorSearchIndex(mongoTemplate);
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
                                .append("numDimensions", 384)
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
}
