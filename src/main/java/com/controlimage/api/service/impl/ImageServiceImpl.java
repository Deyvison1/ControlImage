package com.controlimage.api.service.impl;

import com.controlimage.api.dto.ImageDTO;
import com.controlimage.api.mapper.IImageMapper;
import com.controlimage.api.model.Image;
import com.controlimage.api.repository.IImageRepository;
import com.controlimage.api.service.IImageService;
import com.shareddtos.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {

	private static final String BUCKET = "images";

	@Value("${storage.s3.public-url}")
	private String publicUrl;

	private final IImageRepository repository;
	private final IImageMapper mapper;
	private final S3Client s3Client;

	@Override
	@Transactional
	public ImageDTO add(MultipartFile file, Boolean active) {
		boolean isActive = active == null || active;

		try {
			String hash = DigestUtils.sha256Hex(file.getBytes());

			Optional<Image> existing = repository.findByImageHash(hash);

			if (existing.isPresent()) {
				Image image = existing.get();

				ImageDTO dto = mapper.toDto(image);
				dto.setUrl(getImageUrl(image.getId()));
				return dto;
			}

			String fileKey = generateFileKey(hash + "-" + file.getOriginalFilename());

			s3Client.putObject(
					PutObjectRequest.builder().bucket(BUCKET).key(fileKey).contentType(file.getContentType()).build(),
					RequestBody.fromBytes(file.getBytes()));

			Image entity = Image.builder().filename(file.getOriginalFilename()).objectKey(fileKey).bucket(BUCKET)
					.contentType(file.getContentType()).size(file.getSize()).active(isActive).imageHash(hash).build();

			Image entitySaved = repository.save(entity);

			ImageDTO dto = mapper.toDto(entitySaved);
			dto.setUrl(getImageUrl(entitySaved.getId()));

			return dto;

		} catch (Exception e) {
			throw new RuntimeException("Erro ao fazer upload da imagem", e);
		}
	}

	@Override
	@Transactional
	public void remove(UUID id) {

		Image entity = findById(id);

		try {
			repository.delete(entity);

			s3Client.deleteObject(
					DeleteObjectRequest.builder().bucket(entity.getBucket()).key(entity.getObjectKey()).build());

		} catch (Exception e) {
			throw new RuntimeException("Erro ao deletar imagem", e);
		}
	}

	@Override
	public ImageDTO findByIdToDto(UUID id) {
		return mapper.toDto(findById(id));
	}

	@Override
	public String getImageUrl(UUID id) {

		Image image = findById(id);

		return String.format("%s/%s/%s", publicUrl, image.getBucket(), image.getObjectKey());
	}

	@Override
	@Transactional
	public void activeImage(UUID id) {
		Image entity = findById(id);
		entity.setActive(true);
		repository.save(entity);
	}

	@Override
	@Transactional
	public void disabledImages(List<UUID> ids) {

		List<Image> entities = repository.findAllById(ids);

		if (entities.isEmpty()) {
			return;
		}

		entities.forEach(e -> e.setActive(false));

		repository.saveAll(entities);
	}

	private Image findById(UUID id) {
		return repository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
	}

	private String generateFileKey(String originalName) {
		return UUID.randomUUID() + "-" + originalName;
	}
}