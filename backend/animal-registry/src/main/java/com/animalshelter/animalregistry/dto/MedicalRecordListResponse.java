package com.animalshelter.animalregistry.dto;

import java.util.List;

public record MedicalRecordListResponse(
        boolean success,
        List<MedicalRecordResponse> records,
        long total
) {}
