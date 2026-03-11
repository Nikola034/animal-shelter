package com.animalshelter.animalregistry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryEntry {

    private AnimalStatus status;
    private LocalDateTime changedAt;
    private String changedBy;
    private String changedByUsername;
    private String note;
}
