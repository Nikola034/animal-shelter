package com.animalshelter.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTypeStats {
    private String activityType;
    private long totalMinutes;
    private long count;
}
