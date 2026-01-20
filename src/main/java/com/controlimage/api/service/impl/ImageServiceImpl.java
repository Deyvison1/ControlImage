package com.controlimage.api.service.impl;

import com.controlimage.api.dto.ImageDTO;
import com.controlimage.api.exception.NotFoundException;
import com.controlimage.api.mapper.IImageMapper;
import com.controlimage.api.model.Image;
import com.controlimage.api.repository.IImageRepository;
import com.controlimage.api.service.IImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementação do serviço responsável por gerenciar o ciclo de vida das
 * imagens, incluindo upload, ativação e remoção.
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {

	private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

	private final IImageRepository repository;
	private final IImageMapper mapper;

	/**
	 * Realiza o upload de um arquivo de imagem, salvando-o localmente e persistindo
	 * os metadados no banco de dados.
	 *
	 * @param file arquivo enviado pelo cliente.
	 * @return DTO com os dados da imagem salva.
	 * @throws RuntimeException caso ocorra erro ao salvar o arquivo.
	 */
	@Override
	@Transactional
	public ImageDTO add(MultipartFile file, Boolean active) {
		try {

			// 1) Garante que a pasta existe
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// 2) Gera hash da imagem
			String hash = generateFileHash(file);

			// 3) Descobre a extensão original
			String originalName = file.getOriginalFilename();
			String extension = "";

			if (originalName != null && originalName.contains(".")) {
				extension = originalName.substring(originalName.lastIndexOf("."));
			}

			// 4) Nome final com hash
			String filename = hash + extension;
			Path filePath = uploadPath.resolve(filename);

			// 5) Se já existe no disco, retorna registro existente
			if (Files.exists(filePath)) {
				Image existing = repository.findByFilename(filename)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
								"Arquivo existe no disco, mas não existe no banco."));
				return mapper.toDto(existing);
			}

			// 6) Salva o arquivo fisicamente
			Files.copy(file.getInputStream(), filePath);

			// 7) Cria entidade
			String url = "/images/" + filename;
			Image entity = Image.builder().filename(filename).url(url).contentType(file.getContentType())
					.size(file.getSize()).active(active).build();

			// 8) Persiste
			repository.save(entity);
			return mapper.toDto(entity);

		} catch (IOException e) {
			throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
		}
	}

	private String generateFileHash(MultipartFile file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(file.getBytes());

			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();

		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar hash do arquivo", e);
		}
	}

	/**
	 * Remove uma imagem do sistema com base em seu identificador.
	 *
	 * @param id identificador único da imagem.
	 * @throws NotFoundException caso a imagem não seja encontrada.
	 */
	@Override
	@Transactional
	public void remove(UUID id) {
		Image entity = findById(id);

		try {
			// Monta o caminho completo do arquivo
			Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
			Path filePath = uploadPath.resolve(entity.getFilename());

			// Tenta excluir o arquivo físico, se existir
			Files.deleteIfExists(filePath);

			// Depois remove o registro do banco
			repository.delete(entity);

		} catch (IOException e) {
			throw new RuntimeException("Erro ao deletar o arquivo: " + e.getMessage(), e);
		}
	}

	/**
	 * Busca uma imagem pelo ID e converte a entidade para um DTO.
	 *
	 * @param id identificador único da imagem.
	 * @return DTO representando a imagem encontrada.
	 * @throws NotFoundException caso a imagem não seja encontrada.
	 */
	@Override
	public ImageDTO findByIdToDto(UUID id) {
		return mapper.toDto(findById(id));
	}

	/**
	 * Ativa uma imagem no sistema, marcando-a como ativa no banco de dados.
	 *
	 * @param id identificador da imagem a ser ativada.
	 * @throws NotFoundException caso a imagem não seja encontrada.
	 */
	@Transactional
	@Override
	public void activeImage(UUID id) {
		Image entity = findById(id);
		entity.setActive(true);
		repository.save(entity);
	}

	/**
	 * Busca uma entidade de imagem pelo identificador.
	 *
	 * @param id identificador da imagem.
	 * @return entidade {@link Image}.
	 * @throws NotFoundException caso a imagem não seja encontrada.
	 */
	private Image findById(UUID id) {
		return repository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
	}

	@Override
	public void disabledImages(List<UUID> ids) {
		List<Image> entities = repository.findAllById(ids);
		if (Objects.nonNull(entities) && !entities.isEmpty()) {
			entities.forEach(e -> {
				e.setActive(false);
				repository.save(e);
			});
		}
	}
}
