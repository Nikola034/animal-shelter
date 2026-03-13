package com.animalshelter.activitytracking.repository;

import com.animalshelter.activitytracking.model.ActivityRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActivityRecordRepository extends MongoRepository<ActivityRecord, String> {

    List<ActivityRecord> findByAnimalIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            String animalId, Instant from, Instant to);

    List<ActivityRecord> findByAnimalIdOrderByRecordedAtDesc(String animalId);

    void deleteAllByAnimalId(String animalId);
}
