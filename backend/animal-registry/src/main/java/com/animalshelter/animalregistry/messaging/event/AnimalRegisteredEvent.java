package com.animalshelter.animalregistry.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalRegisteredEvent {
    private String animalId;
    private String name;
    private String category;
    private String breed;
    private String gender;
    private Integer ageMonths;
    private Double weight;
    private String registeredBy;
    private String registeredByUsername;
    private LocalDateTime timestamp;
}
