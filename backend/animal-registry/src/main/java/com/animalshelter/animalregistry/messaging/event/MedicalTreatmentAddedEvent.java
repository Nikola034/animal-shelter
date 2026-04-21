package com.animalshelter.animalregistry.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalTreatmentAddedEvent {
    private String recordId;
    private String animalId;
    private String type;
    private String title;
    private String description;
    private LocalDate date;
    private String veterinarianId;
    private String veterinarianName;
    private LocalDateTime timestamp;
}
