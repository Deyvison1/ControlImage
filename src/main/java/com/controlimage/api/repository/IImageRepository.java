package com.controlimage.api.repository;

import com.controlimage.api.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IImageRepository extends JpaRepository<Image, UUID> {
	Optional<Image> findByFilename(String filename);
}
