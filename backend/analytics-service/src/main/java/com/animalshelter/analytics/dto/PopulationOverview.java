package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopulationOverview {
    private long totalAnimals;
    private List<CategoryCount> byCategory;
    private List<StatusCount> byStatus;
    private List<GenderCount> byGender;
    private List<AgeGroupCount> ageDistribution;
}
