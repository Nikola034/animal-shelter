package com.animalshelter.animalregistry.repository;

import com.animalshelter.animalregistry.model.Animal;
import com.animalshelter.animalregistry.model.AnimalCategory;
import com.animalshelter.animalregistry.model.AnimalStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {

    List<Animal> findByCategory(AnimalCategory category);

    List<Animal> findByStatus(AnimalStatus status);

    Optional<Animal> findByChipId(String chipId);

    boolean existsByChipId(String chipId);

    List<Animal> findByNameContainingIgnoreCase(String name);
}
