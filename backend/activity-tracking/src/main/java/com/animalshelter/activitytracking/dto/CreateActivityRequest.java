package com.animalshelter.activitytracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateActivityRequest {

    @NotBlank(message = "Animal ID is required")
    private String animalId;

    @NotBlank(message = "Activity type is required")
    private String activityType;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private String notes;

    @NotNull(message = "Recorded at time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Belgrade")
    private Instant recordedAt;
}
