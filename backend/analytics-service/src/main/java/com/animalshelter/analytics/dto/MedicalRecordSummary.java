package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordSummary {
    private String animalId;
    private String animalName;
    private String type;
    private String title;
    private String description;
    private String date;
    private String veterinarianName;
    private String notes;
}
