package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightDataPoint {
    private String date;
    private Double weightGrams;
}
