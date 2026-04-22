package com.animalshelter.animalregistry.dto;

import java.util.List;

public record RagSearchResponse(
        String query,
        String answer,
        List<MatchedAnimal> matchedAnimals,
        int totalMatches
) {
    public record MatchedAnimal(
            String id,
            String name,
            String category,
            String breed,
            String status,
            String description,
            double relevanceScore
    ) {}
}
