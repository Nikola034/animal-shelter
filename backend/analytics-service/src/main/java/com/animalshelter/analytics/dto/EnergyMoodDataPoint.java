package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyMoodDataPoint {
    private String date;
    private Integer energyLevel;
    private Integer moodLevel;
}
