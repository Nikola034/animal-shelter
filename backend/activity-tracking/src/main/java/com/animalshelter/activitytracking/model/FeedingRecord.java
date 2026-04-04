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

@Document(collection = "feedings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedingRecord {

    @Id
    private String id;

    @Indexed
    @Field("animal_id")
    private String animalId;

    @Field("food_type")
    private FoodType foodType;

    @Field("quantity_grams")
    private Double quantityGrams;

    @Field("meal_time")
    private Instant mealTime;

    private String notes;

    @Field("recorded_by")
    private String recordedBy;

    @Field("recorded_by_name")
    private String recordedByName;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
}
