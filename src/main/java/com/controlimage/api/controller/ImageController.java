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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ImageConstants.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = ImageConstants.TITLE, description = ImageConstants.DESCRIPTION)
public class ImageController {

	private final IImageService service;

	/**
	 * Faz upload de uma nova imagem. Salva o arquivo recebido e retorna os
	 * metadados da imagem armazenada.
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Faz upload de uma nova imagem", description = "Salva o arquivo recebido no diretório de uploads e retorna seus metadados.", responses = {
			@ApiResponse(responseCode = "201", description = ImageConstants.UPLOAD, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
			@ApiResponse(responseCode = "400", description = "Arquivo inválido"),
			@ApiResponse(responseCode = "500", description = "Erro interno ao processar o upload") })
	public ResponseEntity<ImageDTO> upload(
			@Parameter(description = "Arquivo da imagem a ser enviada", required = true) @RequestPart("file") MultipartFile file,
			@RequestParam(defaultValue = "false", required = false) Boolean active) {
		ImageDTO saved = service.add(file, active);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	/**
	 * Retorna a imagem binária armazenada no servidor.
	 */
	@GetMapping("/{id}")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Faz download de uma imagem", description = "Recupera e retorna o conteúdo binário da imagem pelo seu ID.", responses = {
			@ApiResponse(responseCode = "201", description = ImageConstants.DOWNLOAD, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
			@ApiResponse(responseCode = "404", description = "Imagem não encontrada") })
	public ResponseEntity<ImageDTO> getImage(
			@Parameter(description = "ID da imagem", required = true) @PathVariable UUID id) throws IOException {
		ImageDTO dto = service.findByIdToDto(id);
		return ResponseEntity.ok(dto);
	}

	/**
	 * Retorna a imagem binária armazenada no servidor.
	 */
	@GetMapping("/{id}/download")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Faz download de uma imagem", description = "Recupera e retorna o conteúdo binário da imagem pelo seu ID.", responses = {
			@ApiResponse(responseCode = "200", description = ImageConstants.DOWNLOAD, content = @Content(mediaType = "application/octet-stream")),
			@ApiResponse(responseCode = "404", description = "Imagem não encontrada") })
	public ResponseEntity<Resource> download(
			@Parameter(description = "ID da imagem", required = true) @PathVariable UUID id) throws IOException {
		ImageDTO dto = service.findByIdToDto(id);

		Path path = Paths.get(System.getProperty("user.dir"), "uploads", dto.getFilename());
		if (!Files.exists(path)) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new UrlResource(path.toUri());
		String contentType = Files.probeContentType(path);
		if (contentType == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
	}

	/**
	 * Ativa uma imagem existente, definindo-a como ativa no sistema.
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Remove uma imagem", description = "Remove a imagem pelo id.", responses = {
			@ApiResponse(responseCode = "200", description = ImageConstants.DELETED),
			@ApiResponse(responseCode = "404", description = "Imagem não encontrada") })
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID da imagem a ser removida", required = true) @PathVariable UUID id) {
		service.remove(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * Ativa uma imagem existente, definindo-a como ativa no sistema.
	 */
	@PutMapping("/{id}/activate")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Ativa uma imagem", description = "Define a imagem como ativa com base no seu ID.", responses = {
			@ApiResponse(responseCode = "200", description = ImageConstants.ACTIVE_IMAGE),
			@ApiResponse(responseCode = "404", description = "Imagem não encontrada") })
	public ResponseEntity<Void> activateImage(
			@Parameter(description = "ID da imagem a ser ativada", required = true) @PathVariable UUID id) {
		service.activeImage(id);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * Ativa uma imagem existente, definindo-a como ativa no sistema.
	 */
	@PutMapping("/{ids}/disabled")
	@PreAuthorize(ImageConstants.ADMIN_AUTHORITY)
	@Operation(summary = "Ativa uma imagem", description = "Define a imagem como ativa com base no seu ID.", responses = {
			@ApiResponse(responseCode = "200", description = ImageConstants.ACTIVE_IMAGE),
			@ApiResponse(responseCode = "404", description = "Imagem não encontrada") })
	public ResponseEntity<Void> activateImage(
			@Parameter(description = "ID da imagem a ser ativada", required = true) @PathVariable List<UUID> ids) {
		service.disabledImages(ids);
		return ResponseEntity.ok().build();
	}
}
