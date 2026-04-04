package com.animalshelter.activitytracking.dto;

import com.animalshelter.activitytracking.model.ActivityRecord;
import com.animalshelter.activitytracking.model.ActivityType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRecordResponse {

    private String id;
    private String animalId;
    private ActivityType activityType;
    private Integer durationMinutes;
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Belgrade")
    private Instant recordedAt;

    private String recordedBy;
    private String recordedByName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Belgrade")
    private Instant createdAt;

    public static ActivityRecordResponse fromEntity(ActivityRecord a) {
        ActivityRecordResponse r = new ActivityRecordResponse();
        r.setId(a.getId());
        r.setAnimalId(a.getAnimalId());
        r.setActivityType(a.getActivityType());
        r.setDurationMinutes(a.getDurationMinutes());
        r.setNotes(a.getNotes());
        r.setRecordedAt(a.getRecordedAt());
        r.setRecordedBy(a.getRecordedBy());
        r.setRecordedByName(a.getRecordedByName());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
