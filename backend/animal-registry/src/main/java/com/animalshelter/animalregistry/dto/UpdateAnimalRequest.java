package com.animalshelter.animalregistry.dto;

import com.animalshelter.animalregistry.model.AnimalCategory;
import com.animalshelter.animalregistry.model.Gender;
import jakarta.validation.constraints.Size;

public record UpdateAnimalRequest(
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        AnimalCategory category,

        String breed,

        Gender gender,

        Integer ageMonths,

        Double weight,

        String color,

        String chipId,

        String description
) {}
