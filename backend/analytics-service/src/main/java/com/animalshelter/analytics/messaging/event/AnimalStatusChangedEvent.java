package com.animalshelter.analytics.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalStatusChangedEvent {
    private String animalId;
    private String animalName;
    private String previousStatus;
    private String newStatus;
    private String changedBy;
    private String changedByUsername;
    private String note;
    private LocalDateTime timestamp;
}
