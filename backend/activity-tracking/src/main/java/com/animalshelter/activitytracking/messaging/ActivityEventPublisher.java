package com.animalshelter.activitytracking.messaging;

import com.animalshelter.activitytracking.config.RabbitMQConfig;
import com.animalshelter.activitytracking.messaging.event.DailyMetricsRecordedEvent;
import com.animalshelter.activitytracking.messaging.event.FeedingRecordedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        log.info("========== ActivityEventPublisher initialized - RabbitMQ messaging ready ==========");
    }

    public void publishDailyMetricsRecorded(DailyMetricsRecordedEvent event) {
        try {
            log.info("========== PUBLISHING EVENT: DailyMetricsRecorded ==========");
            log.info("Animal ID: {}, Date: {}, Weight: {}g, Energy: {}, Mood: {}",
                    event.getAnimalId(), event.getDate(), event.getWeightGrams(),
                    event.getEnergyLevel(), event.getMoodLevel());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.DAILY_METRICS_RECORDED_KEY,
                    event
            );
            log.info("========== EVENT SENT SUCCESSFULLY: DailyMetricsRecorded for animal {} ==========", event.getAnimalId());
        } catch (Exception e) {
            log.error("========== FAILED TO SEND EVENT: DailyMetricsRecorded for animal {} ==========", event.getAnimalId(), e);
        }
    }

    public void publishFeedingRecorded(FeedingRecordedEvent event) {
        try {
            log.info("========== PUBLISHING EVENT: FeedingRecorded ==========");
            log.info("Animal ID: {}, Food: {}, Quantity: {}g",
                    event.getAnimalId(), event.getFoodType(), event.getQuantityGrams());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.FEEDING_RECORDED_KEY,
                    event
            );
            log.info("========== EVENT SENT SUCCESSFULLY: FeedingRecorded for animal {} ==========", event.getAnimalId());
        } catch (Exception e) {
            log.error("========== FAILED TO SEND EVENT: FeedingRecorded for animal {} ==========", event.getAnimalId(), e);
        }
    }
}
