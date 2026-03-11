package com.animalshelter.animalregistry.service;

import com.animalshelter.animalregistry.exception.ImageStorageException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class);

    private final Path uploadDir;
    private final List<String> allowedTypes;

    public ImageStorageService(
            @Value("${app.upload.dir}") String uploadDir,
            @Value("${app.upload.allowed-types}") String allowedTypes
    ) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedTypes = Arrays.asList(allowedTypes.split(","));
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadDir);
            log.info("Upload directory initialized: {}", uploadDir);
        } catch (IOException e) {
            throw new ImageStorageException("Could not create upload directory", e);
        }
    }

    public List<String> saveImages(String animalId, List<MultipartFile> files) {
        List<String> savedPaths = new ArrayList<>();

        Path animalDir = uploadDir.resolve(animalId);
        try {
            Files.createDirectories(animalDir);
        } catch (IOException e) {
            throw new ImageStorageException("Could not create directory for animal: " + animalId, e);
        }

        for (MultipartFile file : files) {
            validateFile(file);

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID() + "." + extension;

            Path targetPath = animalDir.resolve(newFilename);

            try {
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                String relativePath = animalId + "/" + newFilename;
                savedPaths.add(relativePath);
                log.info("Image saved: {}", relativePath);
            } catch (IOException e) {
                throw new ImageStorageException("Failed to save image: " + originalFilename, e);
            }
        }

        return savedPaths;
    }

    public void deleteImage(String relativePath) {
        try {
            Path filePath = uploadDir.resolve(relativePath).normalize();
            Files.deleteIfExists(filePath);
            log.info("Image deleted: {}", relativePath);
        } catch (IOException e) {
            log.error("Failed to delete image: {}", relativePath, e);
        }
    }

    public void deleteAllForAnimal(String animalId) {
        Path animalDir = uploadDir.resolve(animalId);
        if (Files.exists(animalDir)) {
            try {
                Files.walk(animalDir)
                        .sorted(java.util.Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                log.error("Failed to delete: {}", path, e);
                            }
                        });
                log.info("All images deleted for animal: {}", animalId);
            } catch (IOException e) {
                log.error("Failed to delete directory for animal: {}", animalId, e);
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ImageStorageException("Cannot upload empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new ImageStorageException("File type not allowed: " + contentType
                    + ". Allowed types: " + String.join(", ", allowedTypes));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
