package com.animalshelter.animalregistry.service;

import com.animalshelter.animalregistry.config.UserContext;
import com.animalshelter.animalregistry.dto.*;
import com.animalshelter.animalregistry.exception.ResourceNotFoundException;
import com.animalshelter.animalregistry.messaging.AnimalEventPublisher;
import com.animalshelter.animalregistry.messaging.event.AnimalRegisteredEvent;
import com.animalshelter.animalregistry.messaging.event.AnimalStatusChangedEvent;
import com.animalshelter.animalregistry.model.Animal;
import com.animalshelter.animalregistry.model.AnimalCategory;
import com.animalshelter.animalregistry.model.AnimalStatus;
import com.animalshelter.animalregistry.model.StatusHistoryEntry;
import com.animalshelter.animalregistry.repository.AnimalRepository;
import com.animalshelter.animalregistry.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MongoTemplate mongoTemplate;
    private final ImageStorageService imageStorageService;
    private final UserContext userContext;
    private final AnimalEventPublisher eventPublisher;
    private final AnimalVectorService animalVectorService;
    private final int maxImagesPerAnimal;

    public AnimalService(
            AnimalRepository animalRepository,
            MedicalRecordRepository medicalRecordRepository,
            MongoTemplate mongoTemplate,
            ImageStorageService imageStorageService,
            UserContext userContext,
            AnimalEventPublisher eventPublisher,
            AnimalVectorService animalVectorService,
            @Value("${app.upload.max-images-per-animal}") int maxImagesPerAnimal
    ) {
        this.animalRepository = animalRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.mongoTemplate = mongoTemplate;
        this.imageStorageService = imageStorageService;
        this.userContext = userContext;
        this.eventPublisher = eventPublisher;
        this.animalVectorService = animalVectorService;
        this.maxImagesPerAnimal = maxImagesPerAnimal;
    }

    public AnimalResponse createAnimal(CreateAnimalRequest request) {
        if (request.chipId() != null && !request.chipId().isBlank()
                && animalRepository.existsByChipId(request.chipId())) {
            throw new IllegalArgumentException("An animal with chip ID " + request.chipId() + " already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        StatusHistoryEntry initialStatus = StatusHistoryEntry.builder()
                .status(AnimalStatus.Active)
                .changedAt(now)
                .changedBy(userContext.getUserId())
                .changedByUsername(userContext.getUsername())
                .note("Animal registered")
                .build();

        Animal animal = Animal.builder()
                .name(request.name())
                .category(request.category())
                .breed(request.breed())
                .gender(request.gender())
                .ageMonths(request.ageMonths())
                .weight(request.weight())
                .color(request.color())
                .chipId(request.chipId() != null && !request.chipId().isBlank() ? request.chipId() : null)
                .status(AnimalStatus.Active)
                .description(request.description())
                .registeredBy(userContext.getUserId())
                .registeredByUsername(userContext.getUsername())
                .createdAt(now)
                .updatedAt(now)
                .build();

        animal.getStatusHistory().add(initialStatus);

        animal = animalRepository.save(animal);

        // Sync to Weaviate vector store for RAG search
        animalVectorService.upsertAnimal(animal);

        eventPublisher.publishAnimalRegistered(AnimalRegisteredEvent.builder()
                .animalId(animal.getId())
                .name(animal.getName())
                .category(animal.getCategory().name())
                .breed(animal.getBreed())
                .gender(animal.getGender() != null ? animal.getGender().name() : null)
                .ageMonths(animal.getAgeMonths())
                .weight(animal.getWeight())
                .registeredBy(userContext.getUserId())
                .registeredByUsername(userContext.getUsername())
                .timestamp(now)
                .build());

        return AnimalResponse.fromEntity(animal);
    }

    public AnimalResponse getAnimalById(String id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));
        return AnimalResponse.fromEntity(animal);
    }

    public AnimalListResponse getAllAnimals() {
        List<Animal> animals = animalRepository.findAll();
        List<AnimalResponse> responses = animals.stream()
                .map(AnimalResponse::fromEntity)
                .toList();
        return new AnimalListResponse(true, responses, responses.size());
    }

    public AnimalListResponse searchAnimals(String name, AnimalCategory category,
                                            AnimalStatus status, String chipId) {
        Query query = new Query();

        if (name != null && !name.isBlank()) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }
        if (category != null) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if (chipId != null && !chipId.isBlank()) {
            query.addCriteria(Criteria.where("chipId").is(chipId));
        }

        List<Animal> animals = mongoTemplate.find(query, Animal.class);
        List<AnimalResponse> responses = animals.stream()
                .map(AnimalResponse::fromEntity)
                .toList();
        return new AnimalListResponse(true, responses, responses.size());
    }

    public AnimalResponse updateAnimal(String id, UpdateAnimalRequest request) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));

        if (request.name() != null) animal.setName(request.name());
        if (request.category() != null) animal.setCategory(request.category());
        if (request.breed() != null) animal.setBreed(request.breed());
        if (request.gender() != null) animal.setGender(request.gender());
        if (request.ageMonths() != null) animal.setAgeMonths(request.ageMonths());
        if (request.weight() != null) animal.setWeight(request.weight());
        if (request.color() != null) animal.setColor(request.color());
        if (request.description() != null) animal.setDescription(request.description());

        if (request.chipId() != null) {
            if (!request.chipId().equals(animal.getChipId())) {
                if (!request.chipId().isBlank() && animalRepository.existsByChipId(request.chipId())) {
                    throw new IllegalArgumentException("An animal with chip ID " + request.chipId() + " already exists");
                }
                animal.setChipId(request.chipId().isBlank() ? null : request.chipId());
            }
        }

        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);

        // Sync updated data to Weaviate
        animalVectorService.upsertAnimal(animal);

        return AnimalResponse.fromEntity(animal);
    }

    public AnimalResponse updateAnimalStatus(String id, UpdateAnimalStatusRequest request) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));

        AnimalStatus previousStatus = animal.getStatus();

        StatusHistoryEntry entry = StatusHistoryEntry.builder()
                .status(request.status())
                .changedAt(LocalDateTime.now())
                .changedBy(userContext.getUserId())
                .changedByUsername(userContext.getUsername())
                .note(request.note())
                .build();

        animal.setStatus(request.status());
        animal.getStatusHistory().add(entry);
        animal.setUpdatedAt(LocalDateTime.now());

        animal = animalRepository.save(animal);

        // Sync status change to Weaviate
        animalVectorService.upsertAnimal(animal);

        eventPublisher.publishAnimalStatusChanged(AnimalStatusChangedEvent.builder()
                .animalId(animal.getId())
                .animalName(animal.getName())
                .previousStatus(previousStatus.name())
                .newStatus(request.status().name())
                .changedBy(userContext.getUserId())
                .changedByUsername(userContext.getUsername())
                .note(request.note())
                .timestamp(LocalDateTime.now())
                .build());

        return AnimalResponse.fromEntity(animal);
    }

    public MessageResponse deleteAnimal(String id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));

        imageStorageService.deleteAllForAnimal(id);
        medicalRecordRepository.deleteByAnimalId(id);
        animalRepository.delete(animal);

        // Remove from Weaviate vector store
        animalVectorService.deleteAnimal(id);

        return new MessageResponse(true, "Animal deleted successfully");
    }

    public ImageUploadResponse uploadImages(String animalId, List<MultipartFile> files) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + animalId));

        int currentCount = animal.getImagePaths() != null ? animal.getImagePaths().size() : 0;
        if (currentCount + files.size() > maxImagesPerAnimal) {
            throw new IllegalArgumentException("Maximum " + maxImagesPerAnimal
                    + " images allowed per animal. Currently has " + currentCount);
        }

        List<String> savedPaths = imageStorageService.saveImages(animalId, files);
        animal.getImagePaths().addAll(savedPaths);
        animal.setUpdatedAt(LocalDateTime.now());
        animalRepository.save(animal);

        return new ImageUploadResponse(true, savedPaths, savedPaths.size() + " image(s) uploaded successfully");
    }

    public MessageResponse deleteImage(String animalId, String imagePath) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + animalId));

        if (!animal.getImagePaths().remove(imagePath)) {
            throw new ResourceNotFoundException("Image path not found: " + imagePath);
        }

        imageStorageService.deleteImage(imagePath);
        animal.setUpdatedAt(LocalDateTime.now());
        animalRepository.save(animal);

        return new MessageResponse(true, "Image deleted successfully");
    }
}
