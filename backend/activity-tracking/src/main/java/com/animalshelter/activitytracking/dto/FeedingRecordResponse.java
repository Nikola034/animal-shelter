package com.animalshelter.activitytracking.dto;

import com.animalshelter.activitytracking.model.FeedingRecord;
import com.animalshelter.activitytracking.model.FoodType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedingRecordResponse {

    private String id;
    private String animalId;
    private FoodType foodType;
    private Double quantityGrams;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Belgrade")
    private Instant mealTime;

    private String notes;
    private String recordedBy;
    private String recordedByName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Belgrade")
    private Instant createdAt;

    public static FeedingRecordResponse fromEntity(FeedingRecord f) {
        FeedingRecordResponse r = new FeedingRecordResponse();
        r.setId(f.getId());
        r.setAnimalId(f.getAnimalId());
        r.setFoodType(f.getFoodType());
        r.setQuantityGrams(f.getQuantityGrams());
        r.setMealTime(f.getMealTime());
        r.setNotes(f.getNotes());
        r.setRecordedBy(f.getRecordedBy());
        r.setRecordedByName(f.getRecordedByName());
        r.setCreatedAt(f.getCreatedAt());
        return r;
    }
}
