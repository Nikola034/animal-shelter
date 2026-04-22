package com.animalshelter.analytics.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMetricsRecordedEvent {
    private String measurementId;
    private String animalId;
    private LocalDate date;
    private Double weightGrams;
    private Double temperatureCelsius;
    private Integer energyLevel;
    private Integer moodLevel;
    private String recordedBy;
    private String recordedByName;
    private LocalDateTime timestamp;
}
