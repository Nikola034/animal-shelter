package com.animalshelter.animalregistry.dto;

import java.util.List;

public record AnimalListResponse(
        boolean success,
        List<AnimalResponse> animals,
        long total
) {}
