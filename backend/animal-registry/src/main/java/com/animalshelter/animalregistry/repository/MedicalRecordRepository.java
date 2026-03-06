package com.animalshelter.animalregistry.repository;

import com.animalshelter.animalregistry.model.MedicalRecord;
import com.animalshelter.animalregistry.model.MedicalRecordType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {

    List<MedicalRecord> findByAnimalIdOrderByDateDesc(String animalId);

    List<MedicalRecord> findByAnimalIdAndType(String animalId, MedicalRecordType type);

    void deleteByAnimalId(String animalId);
}
