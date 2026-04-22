package com.animalshelter.activitytracking.service;

import com.animalshelter.activitytracking.config.UserContext;
import com.animalshelter.activitytracking.dto.*;
import com.animalshelter.activitytracking.exception.AccessDeniedException;
import com.animalshelter.activitytracking.exception.ResourceNotFoundException;
import com.animalshelter.activitytracking.messaging.ActivityEventPublisher;
import com.animalshelter.activitytracking.messaging.event.DailyMetricsRecordedEvent;
import com.animalshelter.activitytracking.messaging.event.FeedingRecordedEvent;
import com.animalshelter.activitytracking.model.*;
import com.animalshelter.activitytracking.repository.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityTrackingService {

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Belgrade");

    private final DailyMeasurementRepository measurementRepository;
    private final ActivityRecordRepository activityRepository;
    private final FeedingRecordRepository feedingRepository;
    private final ObjectProvider<UserContext> userContextProvider;
    private final ActivityEventPublisher eventPublisher;

    public ActivityTrackingService(DailyMeasurementRepository measurementRepository,
                                   ActivityRecordRepository activityRepository,
                                   FeedingRecordRepository feedingRepository,
                                   ObjectProvider<UserContext> userContextProvider,
                                   ActivityEventPublisher eventPublisher) {
        this.measurementRepository = measurementRepository;
        this.activityRepository = activityRepository;
        this.feedingRepository = feedingRepository;
        this.userContextProvider = userContextProvider;
        this.eventPublisher = eventPublisher;
    }

    // ══════════════════════════════════════════════════════════
    //  MEASUREMENTS
    // ══════════════════════════════════════════════════════════

    public DailyMeasurementResponse saveMeasurement(SaveMeasurementRequest request) {
        requireCaretakerRole();
        UserContext ctx = userContextProvider.getObject();

        DailyMeasurement measurement = measurementRepository
                .findByAnimalIdAndDate(request.getAnimalId(), request.getDate())
                .orElseGet(() -> {
                    DailyMeasurement m = new DailyMeasurement();
                    m.setAnimalId(request.getAnimalId());
                    m.setDate(request.getDate());
                    m.setCreatedBy(ctx.getUserId());
                    m.setCreatedByName(ctx.getUsername());
                    return m;
                });

        if (request.getWeightGrams() != null) measurement.setWeightGrams(request.getWeightGrams());
        if (request.getTemperatureCelsius() != null) measurement.setTemperatureCelsius(request.getTemperatureCelsius());
        if (request.getEnergyLevel() != null) measurement.setEnergyLevel(request.getEnergyLevel());
        if (request.getMoodLevel() != null) measurement.setMoodLevel(request.getMoodLevel());

        DailyMeasurement saved = measurementRepository.save(measurement);

        eventPublisher.publishDailyMetricsRecorded(DailyMetricsRecordedEvent.builder()
                .measurementId(saved.getId())
                .animalId(saved.getAnimalId())
                .date(saved.getDate())
                .weightGrams(saved.getWeightGrams())
                .temperatureCelsius(saved.getTemperatureCelsius())
                .energyLevel(saved.getEnergyLevel())
                .moodLevel(saved.getMoodLevel())
                .recordedBy(ctx.getUserId())
                .recordedByName(ctx.getUsername())
                .timestamp(LocalDateTime.now())
                .build());

        return DailyMeasurementResponse.fromEntity(saved);
    }

    public DailyMeasurementResponse getMeasurement(String animalId, LocalDate date) {
        DailyMeasurement measurement = measurementRepository
                .findByAnimalIdAndDate(animalId, date)
                .orElse(null);
        return measurement != null ? DailyMeasurementResponse.fromEntity(measurement) : null;
    }

    public void deleteMeasurement(String id) {
        requireAdminRole();
        DailyMeasurement m = measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Measurement not found: " + id));
        measurementRepository.delete(m);
    }

    // ══════════════════════════════════════════════════════════
    //  ACTIVITIES
    // ══════════════════════════════════════════════════════════

    public ActivityRecordResponse addActivity(CreateActivityRequest request) {
        requireCaretakerRole();
        UserContext ctx = userContextProvider.getObject();

        ActivityRecord record = new ActivityRecord();
        record.setAnimalId(request.getAnimalId());
        record.setActivityType(request.getActivityType());
        record.setDurationMinutes(request.getDurationMinutes());
        record.setNotes(request.getNotes());
        record.setRecordedAt(request.getRecordedAt());
        record.setRecordedBy(ctx.getUserId());
        record.setRecordedByName(ctx.getUsername());

        return ActivityRecordResponse.fromEntity(activityRepository.save(record));
    }

    public List<ActivityRecordResponse> getActivitiesByAnimalAndDate(String animalId, LocalDate date) {
        Instant startOfDay = date.atStartOfDay(APP_ZONE).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(APP_ZONE).toInstant();

        return activityRepository
                .findByAnimalIdAndRecordedAtBetweenOrderByRecordedAtAsc(animalId, startOfDay, endOfDay)
                .stream()
                .map(ActivityRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteActivity(String id) {
        requireCaretakerRole();
        ActivityRecord record = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));
        activityRepository.delete(record);
    }

    // ══════════════════════════════════════════════════════════
    //  FEEDINGS
    // ══════════════════════════════════════════════════════════

    public FeedingRecordResponse addFeeding(CreateFeedingRequest request) {
        requireCaretakerRole();
        UserContext ctx = userContextProvider.getObject();

        FeedingRecord record = new FeedingRecord();
        record.setAnimalId(request.getAnimalId());
        record.setFoodType(request.getFoodType());
        record.setQuantityGrams(request.getQuantityGrams());
        record.setMealTime(request.getMealTime());
        record.setNotes(request.getNotes());
        record.setRecordedBy(ctx.getUserId());
        record.setRecordedByName(ctx.getUsername());

        FeedingRecord saved = feedingRepository.save(record);

        eventPublisher.publishFeedingRecorded(FeedingRecordedEvent.builder()
                .feedingId(saved.getId())
                .animalId(saved.getAnimalId())
                .foodType(saved.getFoodType().name())
                .quantityGrams(saved.getQuantityGrams())
                .mealTime(saved.getMealTime())
                .recordedBy(ctx.getUserId())
                .recordedByName(ctx.getUsername())
                .timestamp(LocalDateTime.now())
                .build());

        return FeedingRecordResponse.fromEntity(saved);
    }

    public List<FeedingRecordResponse> getFeedingsByAnimalAndDate(String animalId, LocalDate date) {
        Instant startOfDay = date.atStartOfDay(APP_ZONE).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(APP_ZONE).toInstant();

        return feedingRepository
                .findByAnimalIdAndMealTimeBetweenOrderByMealTimeAsc(animalId, startOfDay, endOfDay)
                .stream()
                .map(FeedingRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteFeeding(String id) {
        requireCaretakerRole();
        FeedingRecord record = feedingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feeding not found: " + id));
        feedingRepository.delete(record);
    }

    // ══════════════════════════════════════════════════════════
    //  SECURITY HELPERS
    // ══════════════════════════════════════════════════════════

    private void requireCaretakerRole() {
        UserContext ctx = userContextProvider.getObject();
        String role = ctx.getRole();
        if (!"Admin".equals(role) && !"Caretaker".equals(role)) {
            throw new AccessDeniedException("Only Admin or Caretaker can perform this action");
        }
    }

    private void requireAdminRole() {
        UserContext ctx = userContextProvider.getObject();
        if (!"Admin".equals(ctx.getRole())) {
            throw new AccessDeniedException("Only Admin can perform this action");
        }
    }
}
