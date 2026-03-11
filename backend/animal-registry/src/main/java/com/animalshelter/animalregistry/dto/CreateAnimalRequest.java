package com.animalshelter.animalregistry.dto;

import com.animalshelter.animalregistry.model.AnimalCategory;
import com.animalshelter.animalregistry.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAnimalRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotNull(message = "Category is required")
        AnimalCategory category,

        String breed,

        @NotNull(message = "Gender is required")
        Gender gender,

        Integer ageMonths,

        Double weight,

        String color,

        String chipId,

        String description
) {}
