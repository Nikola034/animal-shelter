package com.animalshelter.animalregistry.dto;

import java.util.List;

public record ImageUploadResponse(
        boolean success,
        List<String> imagePaths,
        String message
) {}
