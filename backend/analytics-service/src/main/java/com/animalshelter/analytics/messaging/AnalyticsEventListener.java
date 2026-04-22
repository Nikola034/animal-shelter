package com.animalshelter.analytics.messaging;

import com.animalshelter.analytics.config.RabbitMQConfig;
import com.animalshelter.analytics.messaging.event.*;
import com.animalshelter.analytics.model.AnalyticsEventLog;
import com.animalshelter.analytics.repository.AnalyticsEventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventListener {

    private final AnalyticsEventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        log.info("========== AnalyticsEventListener initialized - listening for RabbitMQ events ==========");
    }

    @RabbitListener(queues = RabbitMQConfig.ANIMAL_REGISTERED_QUEUE)
    public void handleAnimalRegistered(AnimalRegisteredEvent event) {
        log.info("========== RECEIVED EVENT: AnimalRegistered ==========");
        log.info("Animal: {} (ID: {}), Category: {}, Breed: {}",
                event.getName(), event.getAnimalId(), event.getCategory(), event.getBreed());
        saveEventLog("AnimalRegistered", event.getAnimalId(), event);
        log.info("========== EVENT PROCESSED: AnimalRegistered - saved to event_log ==========");
    }

    @RabbitListener(queues = RabbitMQConfig.ANIMAL_STATUS_CHANGED_QUEUE)
    public void handleAnimalStatusChanged(AnimalStatusChangedEvent event) {
        log.info("========== RECEIVED EVENT: AnimalStatusChanged ==========");
        log.info("Animal: {} (ID: {}), Status: {} -> {}",
                event.getAnimalName(), event.getAnimalId(), event.getPreviousStatus(), event.getNewStatus());
        saveEventLog("AnimalStatusChanged", event.getAnimalId(), event);
        log.info("========== EVENT PROCESSED: AnimalStatusChanged - saved to event_log ==========");
    }

    @RabbitListener(queues = RabbitMQConfig.MEDICAL_TREATMENT_QUEUE)
    public void handleMedicalTreatmentAdded(MedicalTreatmentAddedEvent event) {
        log.info("========== RECEIVED EVENT: MedicalTreatmentAdded ==========");
        log.info("Animal ID: {}, Type: {}, Title: {}", event.getAnimalId(), event.getType(), event.getTitle());
        saveEventLog("MedicalTreatmentAdded", event.getAnimalId(), event);
        log.info("========== EVENT PROCESSED: MedicalTreatmentAdded - saved to event_log ==========");
    }

    @RabbitListener(queues = RabbitMQConfig.DAILY_METRICS_QUEUE)
    public void handleDailyMetricsRecorded(DailyMetricsRecordedEvent event) {
        log.info("========== RECEIVED EVENT: DailyMetricsRecorded ==========");
        log.info("Animal ID: {}, Date: {}, Weight: {}g, Energy: {}, Mood: {}",
                event.getAnimalId(), event.getDate(), event.getWeightGrams(),
                event.getEnergyLevel(), event.getMoodLevel());
        saveEventLog("DailyMetricsRecorded", event.getAnimalId(), event);
        log.info("========== EVENT PROCESSED: DailyMetricsRecorded - saved to event_log ==========");
    }

    @RabbitListener(queues = RabbitMQConfig.FEEDING_RECORDED_QUEUE)
    public void handleFeedingRecorded(FeedingRecordedEvent event) {
        log.info("========== RECEIVED EVENT: FeedingRecorded ==========");
        log.info("Animal ID: {}, Food: {}, Quantity: {}g",
                event.getAnimalId(), event.getFoodType(), event.getQuantityGrams());
        saveEventLog("FeedingRecorded", event.getAnimalId(), event);
        log.info("========== EVENT PROCESSED: FeedingRecorded - saved to event_log ==========");
    }

    @SuppressWarnings("unchecked")
    private void saveEventLog(String eventType, String animalId, Object event) {
        try {
            Map<String, Object> payload = objectMapper.convertValue(event, Map.class);

            AnalyticsEventLog logEntry = AnalyticsEventLog.builder()
                    .eventType(eventType)
                    .animalId(animalId)
                    .payload(payload)
                    .receivedAt(Instant.now())
                    .build();

            eventLogRepository.save(logEntry);
            log.info("Event log saved to MongoDB: type={}, animalId={}", eventType, animalId);
        } catch (Exception e) {
            log.error("========== FAILED to save event log for {}: {} ==========", eventType, e.getMessage(), e);
        }
    }
}
