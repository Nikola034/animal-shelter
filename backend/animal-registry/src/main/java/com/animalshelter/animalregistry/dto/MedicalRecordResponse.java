package com.animalshelter.animalregistry.dto;

import com.animalshelter.animalregistry.model.MedicalRecord;
import com.animalshelter.animalregistry.model.MedicalRecordType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedicalRecordResponse(
        String id,
        String animalId,
        MedicalRecordType type,
        String title,
        String description,
        LocalDate date,
        String veterinarianId,
        String veterinarianName,
        String notes,
        LocalDateTime createdAt
) {
    public static MedicalRecordResponse fromEntity(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.getId(),
                record.getAnimalId(),
                record.getType(),
                record.getTitle(),
                record.getDescription(),
                record.getDate(),
                record.getVeterinarianId(),
                record.getVeterinarianName(),
                record.getNotes(),
                record.getCreatedAt()
        );
    }
}
