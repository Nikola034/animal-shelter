package com.animalshelter.activitytracking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SaveMeasurementRequest {

    @NotBlank(message = "Animal ID is required")
    private String animalId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Min(value = 0, message = "Weight must be positive")
    private Double weightGrams;

    private Double temperatureCelsius;

    @Min(value = 1, message = "Energy level must be at least 1")
    @Max(value = 10, message = "Energy level must be at most 10")
    private Integer energyLevel;

    @Min(value = 1, message = "Mood level must be at least 1")
    @Max(value = 10, message = "Mood level must be at most 10")
    private Integer moodLevel;
}
