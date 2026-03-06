package com.animalshelter.animalregistry.service;

import com.animalshelter.animalregistry.config.UserContext;
import com.animalshelter.animalregistry.dto.*;
import com.animalshelter.animalregistry.exception.ResourceNotFoundException;
import com.animalshelter.animalregistry.model.MedicalRecord;
import com.animalshelter.animalregistry.model.MedicalRecordType;
import com.animalshelter.animalregistry.repository.AnimalRepository;
import com.animalshelter.animalregistry.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AnimalRepository animalRepository;
    private final UserContext userContext;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            AnimalRepository animalRepository,
            UserContext userContext
    ) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.animalRepository = animalRepository;
        this.userContext = userContext;
    }

    public MedicalRecordResponse createRecord(CreateMedicalRecordRequest request) {
        if (!animalRepository.existsById(request.animalId())) {
            throw new ResourceNotFoundException("Animal not found with id: " + request.animalId());
        }

        MedicalRecord record = MedicalRecord.builder()
                .animalId(request.animalId())
                .type(request.type())
                .title(request.title())
                .description(request.description())
                .date(request.date())
                .veterinarianId(userContext.getUserId())
                .veterinarianName(userContext.getUsername())
                .notes(request.notes())
                .createdAt(LocalDateTime.now())
                .build();

        record = medicalRecordRepository.save(record);
        return MedicalRecordResponse.fromEntity(record);
    }

    public MedicalRecordResponse getRecordById(String id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + id));
        return MedicalRecordResponse.fromEntity(record);
    }

    public MedicalRecordListResponse getRecordsByAnimalId(String animalId) {
        List<MedicalRecord> records = medicalRecordRepository.findByAnimalIdOrderByDateDesc(animalId);
        List<MedicalRecordResponse> responses = records.stream()
                .map(MedicalRecordResponse::fromEntity)
                .toList();
        return new MedicalRecordListResponse(true, responses, responses.size());
    }

    public MedicalRecordListResponse getRecordsByAnimalIdAndType(String animalId, MedicalRecordType type) {
        List<MedicalRecord> records = medicalRecordRepository.findByAnimalIdAndType(animalId, type);
        List<MedicalRecordResponse> responses = records.stream()
                .map(MedicalRecordResponse::fromEntity)
                .toList();
        return new MedicalRecordListResponse(true, responses, responses.size());
    }

    public MessageResponse deleteRecord(String id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medical record not found with id: " + id);
        }
        medicalRecordRepository.deleteById(id);
        return new MessageResponse(true, "Medical record deleted successfully");
    }
}
