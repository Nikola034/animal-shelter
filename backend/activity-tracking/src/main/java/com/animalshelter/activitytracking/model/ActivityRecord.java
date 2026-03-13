package com.animalshelter.activitytracking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRecord {

    @Id
    private String id;

    @Indexed
    @Field("animal_id")
    private String animalId;

    @Field("activity_type")
    private String activityType;

    @Field("duration_minutes")
    private Integer durationMinutes;

    private String notes;

    @Field("recorded_at")
    private Instant recordedAt;

    @Field("recorded_by")
    private String recordedBy;

    @Field("recorded_by_name")
    private String recordedByName;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
}
