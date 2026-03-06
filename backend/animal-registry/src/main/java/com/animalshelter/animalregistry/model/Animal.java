package com.animalshelter.animalregistry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "animals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    private String id;

    private String name;

    @Indexed
    private AnimalCategory category;

    private String breed;

    private Gender gender;

    private Integer ageMonths;

    private Double weight;

    private String color;

    @Indexed(unique = true, sparse = true)
    private String chipId;

    @Indexed
    private AnimalStatus status;

    private String description;

    @Builder.Default
    private List<String> imagePaths = new ArrayList<>();

    @Builder.Default
    private List<StatusHistoryEntry> statusHistory = new ArrayList<>();

    private String registeredBy;
    private String registeredByUsername;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
