package com.animalshelter.animalregistry.dto;

import com.animalshelter.animalregistry.model.AnimalStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAnimalStatusRequest(
        @NotNull(message = "Status is required")
        AnimalStatus status,

        String note
) {}
