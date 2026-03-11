package com.animalshelter.animalregistry.controller;

import com.animalshelter.animalregistry.config.UserContext;
import com.animalshelter.animalregistry.dto.*;
import com.animalshelter.animalregistry.exception.AccessDeniedException;
import com.animalshelter.animalregistry.model.AnimalCategory;
import com.animalshelter.animalregistry.model.AnimalStatus;
import com.animalshelter.animalregistry.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;
    private final UserContext userContext;

    public AnimalController(AnimalService animalService, UserContext userContext) {
        this.animalService = animalService;
        this.userContext = userContext;
    }

    @GetMapping
    public ResponseEntity<AnimalListResponse> getAllAnimals() {
        return ResponseEntity.ok(animalService.getAllAnimals());
    }

    @GetMapping("/search")
    public ResponseEntity<AnimalListResponse> searchAnimals(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) AnimalCategory category,
            @RequestParam(required = false) AnimalStatus status,
            @RequestParam(required = false) String chipId
    ) {
        return ResponseEntity.ok(animalService.searchAnimals(name, category, status, chipId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> getAnimalById(@PathVariable String id) {
        return ResponseEntity.ok(animalService.getAnimalById(id));
    }

    @PostMapping
    public ResponseEntity<AnimalResponse> createAnimal(@Valid @RequestBody CreateAnimalRequest request) {
        requireRole("Admin", "Caretaker");
        AnimalResponse response = animalService.createAnimal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponse> updateAnimal(
            @PathVariable String id,
            @Valid @RequestBody UpdateAnimalRequest request
    ) {
        requireRole("Admin", "Caretaker");
        return ResponseEntity.ok(animalService.updateAnimal(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AnimalResponse> updateAnimalStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateAnimalStatusRequest request
    ) {
        requireRole("Admin", "Caretaker");
        return ResponseEntity.ok(animalService.updateAnimalStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteAnimal(@PathVariable String id) {
        requireRole("Admin");
        return ResponseEntity.ok(animalService.deleteAnimal(id));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ImageUploadResponse> uploadImages(
            @PathVariable String id,
            @RequestParam("files") List<MultipartFile> files
    ) {
        requireRole("Admin", "Caretaker");
        ImageUploadResponse response = animalService.uploadImages(id, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<MessageResponse> deleteImage(
            @PathVariable String id,
            @RequestParam("path") String imagePath
    ) {
        requireRole("Admin", "Caretaker");
        return ResponseEntity.ok(animalService.deleteImage(id, imagePath));
    }

    private void requireRole(String... allowedRoles) {
        String currentRole = userContext.getRole();
        for (String role : allowedRoles) {
            if (role.equals(currentRole)) {
                return;
            }
        }
        throw new AccessDeniedException("Access denied. Required role: " + String.join(" or ", allowedRoles));
    }
}
