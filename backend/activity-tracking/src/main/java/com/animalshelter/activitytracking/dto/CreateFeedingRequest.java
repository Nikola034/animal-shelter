package com.animalshelter.activitytracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateFeedingRequest {

    @NotBlank(message = "Animal ID is required")
    private String animalId;

    @NotBlank(message = "Food type is required")
    private String foodType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1 gram")
    private Double quantityGrams;

    @NotNull(message = "Meal time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Belgrade")
    private Instant mealTime;

    private String notes;

    private Boolean consumedFully;
}
