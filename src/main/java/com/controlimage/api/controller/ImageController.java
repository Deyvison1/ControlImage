package com.controlimage.api.controller;

import com.controlimage.api.dto.ImageDTO;
import com.controlimage.api.dto.constants.ImageConstants;
import com.controlimage.api.service.IImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ImageConstants.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = ImageConstants.TITLE, description = ImageConstants.DESCRIPTION)
public class ImageController {

	private final IImageService service;

	/**
	 * Upload de imagem (S3 privado)
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Faz upload de uma nova imagem", responses = {
			@ApiResponse(responseCode = "201", description = ImageConstants.UPLOAD, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
			@ApiResponse(responseCode = "400", description = "Arquivo inválido"),
			@ApiResponse(responseCode = "500", description = "Erro interno") })
	public ResponseEntity<ImageDTO> upload(
			@Parameter(description = "Arquivo da imagem", required = true) @RequestPart("file") MultipartFile file,

			@RequestParam(defaultValue = "true") Boolean active) {
		ImageDTO saved = service.add(file, active);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	/**
	 * Busca imagem (metadados)
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Busca imagem por ID")
	public ResponseEntity<ImageDTO> getImage(
			@Parameter(description = "ID da imagem", required = true) @PathVariable UUID id) {
		return ResponseEntity.ok(service.findByIdToDto(id));
	}

	/**
	 * 👇 NOVO: retorna URL da imagem (S3)
	 */
	@GetMapping("/{id}/url")
	@Operation(summary = "Obtém URL da imagem no S3")
	public ResponseEntity<String> getImageUrl(
			@Parameter(description = "ID da imagem", required = true) @PathVariable UUID id) {
		return ResponseEntity.ok(service.getImageUrl(id));
	}

	/**
	 * Delete (S3 + DB)
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Remove imagem")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.remove(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Ativa imagem
	 */
	@PutMapping("/{id}/activate")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Ativa imagem")
	public ResponseEntity<Void> activate(@PathVariable UUID id) {
		service.activeImage(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * Desativa imagens em lote
	 */
	@PutMapping("/disabled")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Desativa imagens em lote")
	public ResponseEntity<Void> disable(@RequestBody List<UUID> ids) {
		service.disabledImages(ids);
		return ResponseEntity.ok().build();
	}
}