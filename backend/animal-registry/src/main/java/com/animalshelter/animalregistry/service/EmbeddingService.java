package com.animalshelter.animalregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service that calls the text2vec-transformers container REST API
 * to generate 384-dimensional embeddings from text using all-MiniLM-L6-v2 model.
 */
@Service
@Slf4j
public class EmbeddingService {

    private final RestTemplate restTemplate;
    private final String embeddingApiUrl;

    public EmbeddingService(
            RestTemplate restTemplate,
            @Value("${embedding.api.url}") String embeddingApiUrl
    ) {
        this.restTemplate = restTemplate;
        this.embeddingApiUrl = embeddingApiUrl;
    }

    /**
     * Generate an embedding vector for the given text.
     * Calls POST /vectors on the text2vec-transformers container.
     *
     * @param text the text to vectorize
     * @return 384-dimensional embedding vector
     */
    @SuppressWarnings("unchecked")
    public List<Double> getEmbedding(String text) {
        try {
            Map<String, String> request = Map.of("text", text);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    embeddingApiUrl + "/vectors",
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("vector")) {
                throw new RuntimeException("Invalid response from embedding service: no 'vector' field");
            }

            List<Number> vector = (List<Number>) body.get("vector");
            List<Double> embedding = vector.stream()
                    .map(Number::doubleValue)
                    .collect(Collectors.toList());

            log.debug("Generated embedding ({} dimensions) for text: '{}'",
                    embedding.size(), text.substring(0, Math.min(50, text.length())));

            return embedding;

        } catch (Exception e) {
            log.error("========== Failed to generate embedding: {} ==========", e.getMessage());
            throw new RuntimeException("Embedding generation failed", e);
        }
    }
}
