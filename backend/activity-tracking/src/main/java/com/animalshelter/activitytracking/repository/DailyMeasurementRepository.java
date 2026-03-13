package com.animalshelter.activitytracking.repository;

import com.animalshelter.activitytracking.model.DailyMeasurement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMeasurementRepository extends MongoRepository<DailyMeasurement, String> {

    Optional<DailyMeasurement> findByAnimalIdAndDate(String animalId, LocalDate date);

    List<DailyMeasurement> findByAnimalIdOrderByDateDesc(String animalId);

    void deleteAllByAnimalId(String animalId);
}
