package com.animalshelter.animalregistry.messaging;

import com.animalshelter.animalregistry.config.RabbitMQConfig;
import com.animalshelter.animalregistry.messaging.event.AnimalRegisteredEvent;
import com.animalshelter.animalregistry.messaging.event.AnimalStatusChangedEvent;
import com.animalshelter.animalregistry.messaging.event.MedicalTreatmentAddedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnimalEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        log.info("========== AnimalEventPublisher initialized - RabbitMQ messaging ready ==========");
    }

    public void publishAnimalRegistered(AnimalRegisteredEvent event) {
        try {
            log.info("========== PUBLISHING EVENT: AnimalRegistered ==========");
            log.info("Animal: {} (ID: {}), Category: {}, Breed: {}",
                    event.getName(), event.getAnimalId(), event.getCategory(), event.getBreed());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ANIMAL_REGISTERED_KEY,
                    event
            );
            log.info("========== EVENT SENT SUCCESSFULLY: AnimalRegistered for {} ==========", event.getName());
        } catch (Exception e) {
            log.error("========== FAILED TO SEND EVENT: AnimalRegistered for {} ==========", event.getName(), e);
        }
    }

    public void publishAnimalStatusChanged(AnimalStatusChangedEvent event) {
        try {
            log.info("========== PUBLISHING EVENT: AnimalStatusChanged ==========");
            log.info("Animal: {} (ID: {}), Status: {} -> {}",
                    event.getAnimalName(), event.getAnimalId(), event.getPreviousStatus(), event.getNewStatus());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ANIMAL_STATUS_CHANGED_KEY,
                    event
            );
            log.info("========== EVENT SENT SUCCESSFULLY: AnimalStatusChanged for {} ==========", event.getAnimalName());
        } catch (Exception e) {
            log.error("========== FAILED TO SEND EVENT: AnimalStatusChanged for {} ==========", event.getAnimalName(), e);
        }
    }

    public void publishMedicalTreatmentAdded(MedicalTreatmentAddedEvent event) {
        try {
            log.info("========== PUBLISHING EVENT: MedicalTreatmentAdded ==========");
            log.info("Animal ID: {}, Type: {}, Title: {}", event.getAnimalId(), event.getType(), event.getTitle());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.MEDICAL_TREATMENT_ADDED_KEY,
                    event
            );
            log.info("========== EVENT SENT SUCCESSFULLY: MedicalTreatmentAdded for animal {} ==========", event.getAnimalId());
        } catch (Exception e) {
            log.error("========== FAILED TO SEND EVENT: MedicalTreatmentAdded for animal {} ==========", event.getAnimalId(), e);
        }
    }
}
