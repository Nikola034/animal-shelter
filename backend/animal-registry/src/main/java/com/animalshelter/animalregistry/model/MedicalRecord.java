package com.animalshelter.animalregistry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "medical_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    private String id;

    @Indexed
    private String animalId;

    private MedicalRecordType type;

    private String title;

    private String description;

    private LocalDate date;

    private String veterinarianId;
    private String veterinarianName;

    private String notes;

    private LocalDateTime createdAt;
}
