package com.animalshelter.animalregistry.service;

import com.animalshelter.animalregistry.dto.RagSearchResponse;
import com.animalshelter.animalregistry.model.Animal;
import com.animalshelter.animalregistry.repository.AnimalRepository;
import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RagSearchService {

    private final AnimalVectorService animalVectorService;
    private final AnimalRepository animalRepository;
    private final AnthropicClient anthropicClient;

    @Value("${anthropic.model:claude-sonnet-4-6}")
    private String modelId;

    @Value("${anthropic.max-tokens:2048}")
    private long maxTokens;

    @Autowired
    public RagSearchService(
            AnimalVectorService animalVectorService,
            AnimalRepository animalRepository,
            @Autowired(required = false) AnthropicClient anthropicClient
    ) {
        this.animalVectorService = animalVectorService;
        this.animalRepository = animalRepository;
        this.anthropicClient = anthropicClient;
    }

    private static final String SYSTEM_PROMPT = """
            You are an intelligent assistant for an animal shelter management system. \
            Your task is to help shelter staff and visitors find animals based on natural language queries.

            Based on the user's query and the available animal data provided below, you must:
            1. Rank the animals by relevance to the query
            2. Explain briefly why each animal matches or partially matches the query
            3. Be friendly, helpful, and informative
            4. If no animals match well, explain that and suggest what the user might refine in their search

            Provide your response as a clear, structured narrative. For each animal, mention its name, \
            key characteristics (breed, age, color, status), and why it's relevant to the query.

            Respond in the same language as the user's query (if the query is in Serbian, respond in Serbian; \
            if in English, respond in English).
            """;

    /**
     * Perform RAG search: MongoDB vector search → fetch full data → Claude LLM → ranked response.
     */
    public RagSearchResponse search(String query, int limit) {
        log.info("========== RAG SEARCH START: query='{}', limit={} ==========", query, limit);

        // Step 1: Semantic search in MongoDB via $vectorSearch
        List<AnimalVectorService.AnimalSearchResult> searchResults =
                animalVectorService.semanticSearch(query, limit);

        if (searchResults.isEmpty()) {
            log.info("========== RAG SEARCH: No results from MongoDB vector search ==========");
            return new RagSearchResponse(
                    query,
                    "No animals found matching your query. Try using different keywords or a broader description.",
                    Collections.emptyList(),
                    0
            );
        }

        // Step 2: Extract animal IDs and scores
        List<String> animalIds = searchResults.stream()
                .map(AnimalVectorService.AnimalSearchResult::animalId)
                .collect(Collectors.toList());

        Map<String, Double> scoreMap = searchResults.stream()
                .collect(Collectors.toMap(
                        AnimalVectorService.AnimalSearchResult::animalId,
                        AnimalVectorService.AnimalSearchResult::score
                ));

        // Step 3: Fetch full animal entities from MongoDB
        List<Animal> animals = animalRepository.findAllById(animalIds);
        log.info("========== RAG SEARCH: Found {} animals in MongoDB ==========", animals.size());

        // Sort animals by score (highest first) to preserve vector search ranking
        animals.sort((a, b) -> Double.compare(
                scoreMap.getOrDefault(b.getId(), 0.0),
                scoreMap.getOrDefault(a.getId(), 0.0)
        ));

        // Build matched animals list with relevance scores
        List<RagSearchResponse.MatchedAnimal> matchedAnimals = animals.stream()
                .map(animal -> {
                    double score = scoreMap.getOrDefault(animal.getId(), 0.0);
                    return new RagSearchResponse.MatchedAnimal(
                            animal.getId(),
                            animal.getName(),
                            animal.getCategory() != null ? animal.getCategory().name() : null,
                            animal.getBreed(),
                            animal.getStatus() != null ? animal.getStatus().name() : null,
                            animal.getDescription(),
                            Math.round(score * 1000.0) / 1000.0
                    );
                })
                .collect(Collectors.toList());

        // Step 4: Generate LLM response using Claude
        String llmAnswer = generateLlmResponse(query, animals, scoreMap);

        log.info("========== RAG SEARCH COMPLETE: {} matches ==========", matchedAnimals.size());

        return new RagSearchResponse(
                query,
                llmAnswer,
                matchedAnimals,
                matchedAnimals.size()
        );
    }

    /**
     * Build context string from animals for the LLM prompt.
     */
    private String buildAnimalContext(List<Animal> animals, Map<String, Double> scoreMap) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < animals.size(); i++) {
            Animal animal = animals.get(i);
            double score = scoreMap.getOrDefault(animal.getId(), 0.0);

            context.append(String.format("--- Animal %d (relevance: %.1f%%) ---\n", i + 1, score * 100));
            context.append(String.format("Name: %s\n", animal.getName()));
            context.append(String.format("Category: %s\n", animal.getCategory()));
            context.append(String.format("Breed: %s\n", animal.getBreed() != null ? animal.getBreed() : "Unknown"));
            context.append(String.format("Gender: %s\n", animal.getGender()));

            if (animal.getAgeMonths() != null) {
                int years = animal.getAgeMonths() / 12;
                int months = animal.getAgeMonths() % 12;
                String age = years > 0
                        ? years + " year(s)" + (months > 0 ? " " + months + " month(s)" : "")
                        : months + " month(s)";
                context.append(String.format("Age: %s\n", age));
            }

            if (animal.getWeight() != null) {
                context.append(String.format("Weight: %.1f kg\n", animal.getWeight()));
            }
            context.append(String.format("Color: %s\n", animal.getColor() != null ? animal.getColor() : "N/A"));
            context.append(String.format("Status: %s\n", animal.getStatus()));

            if (animal.getDescription() != null && !animal.getDescription().isBlank()) {
                context.append(String.format("Description: %s\n", animal.getDescription()));
            }
            context.append("\n");
        }
        return context.toString();
    }

    /**
     * Generate an intelligent response using Claude API.
     */
    private String generateLlmResponse(String query, List<Animal> animals, Map<String, Double> scoreMap) {
        if (anthropicClient == null) {
            log.warn("========== Anthropic client not available - returning fallback response ==========");
            return buildFallbackResponse(animals);
        }

        try {
            String animalContext = buildAnimalContext(animals, scoreMap);

            String userMessage = String.format(
                    "User query: \"%s\"\n\nAvailable animals:\n%s\n" +
                    "Please analyze these animals and provide a helpful response ranking them by relevance to the query.",
                    query, animalContext
            );

            MessageCreateParams params = MessageCreateParams.builder()
                    .model(modelId)
                    .maxTokens(maxTokens)
                    .system(SYSTEM_PROMPT)
                    .addUserMessage(userMessage)
                    .build();

            log.info("========== Calling Claude API (model: {}) ==========", modelId);
            Message response = anthropicClient.messages().create(params);

            String answer = response.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(textBlock -> textBlock.text())
                    .collect(Collectors.joining());

            log.info("========== Claude API response received ({} chars) ==========", answer.length());
            return answer;

        } catch (Exception e) {
            log.error("========== Claude API call failed: {} ==========", e.getMessage(), e);
            return buildFallbackResponse(animals);
        }
    }

    /**
     * Build a fallback response when the LLM is unavailable.
     */
    private String buildFallbackResponse(List<Animal> animals) {
        if (animals.isEmpty()) {
            return "No animals found matching your query.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(animals.size()).append(" potentially matching animal(s):\n\n");
        for (int i = 0; i < animals.size(); i++) {
            Animal a = animals.get(i);
            sb.append(String.format("%d. %s - %s %s", i + 1, a.getName(),
                    a.getCategory() != null ? a.getCategory().name() : "",
                    a.getBreed() != null ? "(" + a.getBreed() + ")" : ""));
            sb.append(String.format(", Status: %s", a.getStatus() != null ? a.getStatus().name() : "Unknown"));
            if (a.getDescription() != null) {
                sb.append(String.format("\n   %s", a.getDescription()));
            }
            sb.append("\n\n");
        }
        sb.append("(Note: LLM analysis unavailable - showing raw results)");
        return sb.toString();
    }
}
