package com.animalshelter.analytics.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedingRecordedEvent {
    private String feedingId;
    private String animalId;
    private String foodType;
    private Double quantityGrams;
    private Instant mealTime;
    private String recordedBy;
    private String recordedByName;
    private LocalDateTime timestamp;
}
