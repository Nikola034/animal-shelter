package com.animalshelter.analytics.repository;

import com.animalshelter.analytics.model.AnalyticsEventLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnalyticsEventLogRepository extends MongoRepository<AnalyticsEventLog, String> {

    List<AnalyticsEventLog> findByEventType(String eventType);

    List<AnalyticsEventLog> findByAnimalId(String animalId);
}
