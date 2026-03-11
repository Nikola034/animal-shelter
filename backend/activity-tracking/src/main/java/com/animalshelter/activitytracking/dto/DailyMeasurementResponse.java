package com.animalshelter.activitytracking.dto;

import com.animalshelter.activitytracking.model.DailyMeasurement;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMeasurementResponse {

    private String id;
    private String animalId;
    private LocalDate date;
    private Double weightGrams;
    private Double temperatureCelsius;
    private Integer energyLevel;
    private Integer moodLevel;
    private String createdBy;
    private String createdByName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Belgrade")
    private Instant updatedAt;

    public static DailyMeasurementResponse fromEntity(DailyMeasurement m) {
        DailyMeasurementResponse r = new DailyMeasurementResponse();
        r.setId(m.getId());
        r.setAnimalId(m.getAnimalId());
        r.setDate(m.getDate());
        r.setWeightGrams(m.getWeightGrams());
        r.setTemperatureCelsius(m.getTemperatureCelsius());
        r.setEnergyLevel(m.getEnergyLevel());
        r.setMoodLevel(m.getMoodLevel());
        r.setCreatedBy(m.getCreatedBy());
        r.setCreatedByName(m.getCreatedByName());
        r.setUpdatedAt(m.getUpdatedAt());
        return r;
    }
}
