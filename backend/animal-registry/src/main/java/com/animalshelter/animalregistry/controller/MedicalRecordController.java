package com.animalshelter.animalregistry.controller;

import com.animalshelter.animalregistry.config.UserContext;
import com.animalshelter.animalregistry.dto.*;
import com.animalshelter.animalregistry.exception.AccessDeniedException;
import com.animalshelter.animalregistry.model.MedicalRecordType;
import com.animalshelter.animalregistry.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final UserContext userContext;

    public MedicalRecordController(MedicalRecordService medicalRecordService, UserContext userContext) {
        this.medicalRecordService = medicalRecordService;
        this.userContext = userContext;
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<MedicalRecordListResponse> getRecordsByAnimalId(
            @PathVariable String animalId,
            @RequestParam(required = false) MedicalRecordType type
    ) {
        MedicalRecordListResponse response;
        if (type != null) {
            response = medicalRecordService.getRecordsByAnimalIdAndType(animalId, type);
        } else {
            response = medicalRecordService.getRecordsByAnimalId(animalId);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getRecordById(@PathVariable String id) {
        return ResponseEntity.ok(medicalRecordService.getRecordById(id));
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createRecord(
            @Valid @RequestBody CreateMedicalRecordRequest request
    ) {
        requireRole("Admin", "Veterinarian");
        MedicalRecordResponse response = medicalRecordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRecord(@PathVariable String id) {
        requireRole("Admin");
        return ResponseEntity.ok(medicalRecordService.deleteRecord(id));
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
