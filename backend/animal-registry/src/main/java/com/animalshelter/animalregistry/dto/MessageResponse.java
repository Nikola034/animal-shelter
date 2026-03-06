package com.animalshelter.animalregistry.dto;

public record MessageResponse(
        boolean success,
        String message
) {}
