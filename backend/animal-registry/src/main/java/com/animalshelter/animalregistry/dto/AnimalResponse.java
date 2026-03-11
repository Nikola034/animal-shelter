package com.animalshelter.animalregistry.dto;

import com.animalshelter.animalregistry.model.*;

import java.time.LocalDateTime;
import java.util.List;

public record AnimalResponse(
        String id,
        String name,
        AnimalCategory category,
        String breed,
        Gender gender,
        Integer ageMonths,
        Double weight,
        String color,
        String chipId,
        AnimalStatus status,
        String description,
        List<String> imagePaths,
        List<StatusHistoryEntry> statusHistory,
        String registeredBy,
        String registeredByUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AnimalResponse fromEntity(Animal animal) {
        return new AnimalResponse(
                animal.getId(),
                animal.getName(),
                animal.getCategory(),
                animal.getBreed(),
                animal.getGender(),
                animal.getAgeMonths(),
                animal.getWeight(),
                animal.getColor(),
                animal.getChipId(),
                animal.getStatus(),
                animal.getDescription(),
                animal.getImagePaths(),
                animal.getStatusHistory(),
                animal.getRegisteredBy(),
                animal.getRegisteredByUsername(),
                animal.getCreatedAt(),
                animal.getUpdatedAt()
        );
    }
}
