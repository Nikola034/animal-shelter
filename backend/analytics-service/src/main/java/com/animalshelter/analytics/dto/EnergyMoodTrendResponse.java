package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyMoodTrendResponse {
    private String animalId;
    private List<EnergyMoodDataPoint> data;
}
