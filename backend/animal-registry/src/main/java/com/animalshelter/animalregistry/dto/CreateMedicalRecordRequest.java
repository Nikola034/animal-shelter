package com.animalshelter.animalregistry.dto;

import com.animalshelter.animalregistry.model.MedicalRecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateMedicalRecordRequest(
        @NotBlank(message = "Animal ID is required")
        String animalId,

        @NotNull(message = "Record type is required")
        MedicalRecordType type,

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        String description,

        @NotNull(message = "Date is required")
        LocalDate date,

        String notes
) {}
