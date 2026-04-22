package com.animalshelter.animalregistry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RagSearchRequest(
        @NotBlank(message = "Query is required")
        String query,

        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 20, message = "Limit cannot exceed 20")
        Integer limit
) {
    public RagSearchRequest {
        if (limit == null) {
            limit = 5;
        }
    }
}
