package com.animalshelter.activitytracking.repository;

import com.animalshelter.activitytracking.model.FeedingRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FeedingRecordRepository extends MongoRepository<FeedingRecord, String> {

    List<FeedingRecord> findByAnimalIdAndMealTimeBetweenOrderByMealTimeAsc(
            String animalId, Instant from, Instant to);

    List<FeedingRecord> findByAnimalIdOrderByMealTimeDesc(String animalId);

    void deleteAllByAnimalId(String animalId);
}
