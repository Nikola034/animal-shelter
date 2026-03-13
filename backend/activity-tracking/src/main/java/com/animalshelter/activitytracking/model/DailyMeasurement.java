package com.animalshelter.activitytracking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "daily_measurements")
@CompoundIndex(name = "animal_date_idx", def = "{'animal_id': 1, 'date': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMeasurement {

    @Id
    private String id;

    @Indexed
    @Field("animal_id")
    private String animalId;

    private LocalDate date;

    @Field("weight_grams")
    private Double weightGrams;

    @Field("temperature_celsius")
    private Double temperatureCelsius;

    @Field("energy_level")
    private Integer energyLevel;

    @Field("mood_level")
    private Integer moodLevel;

    @Field("created_by")
    private String createdBy;

    @Field("created_by_name")
    private String createdByName;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
}
