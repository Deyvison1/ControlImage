package com.controlimage.api.repository;

import com.controlimage.api.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface IImageRepository extends JpaRepository<Image, UUID> {
	Optional<Image> findByFilename(String filename);
    boolean existsByHash(String hash);
    Optional<Image> findByHash(String hash);
}
