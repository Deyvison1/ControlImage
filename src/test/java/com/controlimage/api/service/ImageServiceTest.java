package com.controlimage.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.controlimage.api.dto.ImageDTO;
import com.controlimage.api.exception.NotFoundException;
import com.controlimage.api.mapper.IImageMapper;
import com.controlimage.api.model.Image;
import com.controlimage.api.repository.IImageRepository;
import com.controlimage.api.service.impl.ImageServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

	@Mock
	private IImageRepository repository;

	@Mock
	private IImageMapper mapper;

	@InjectMocks
	private ImageServiceImpl service;

	private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

	@BeforeEach
	void setup() throws IOException {
		Files.createDirectories(uploadDir);
	}

	@AfterEach
	void cleanup() throws IOException {
		Files.walk(uploadDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(f -> {
			if (f.exists())
				f.delete();
		});
	}

	// ------------------------------------------------------------
	// TESTE 1: add()
	// ------------------------------------------------------------
	@Test
	void shouldUploadNewImageSuccessfully() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());

		Image entity = Image.builder().filename("test.jpg").url("/images/test.jpg").contentType("image/jpeg").size(4L)
				.active(true).build();

		ImageDTO dto = new ImageDTO();
		when(repository.save(any(Image.class))).thenReturn(entity);
		when(mapper.toDto(any(Image.class))).thenReturn(dto);

		ImageDTO result = service.add(file, true);

		assertNotNull(result);
		verify(repository).save(any(Image.class));
		assertTrue(Files.exists(uploadDir.resolve("test.jpg")));
	}

	@Test
	void shouldThrowConflictIfFileAlreadyExists() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "exists.jpg", "image/jpeg", "data".getBytes());
		Files.createFile(uploadDir.resolve("exists.jpg"));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.add(file, true));
		assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
	}

	// ------------------------------------------------------------
	// TESTE 2: remove()
	// ------------------------------------------------------------
	@Test
	void shouldRemoveImageAndDeleteFile() throws Exception {
		Image image = Image.builder().id(UUID.randomUUID()).filename("toDelete.jpg").build();
		Files.createFile(uploadDir.resolve("toDelete.jpg"));

		when(repository.findById(any(UUID.class))).thenReturn(Optional.of(image));

		service.remove(image.getId());

		verify(repository).delete(image);
		assertFalse(Files.exists(uploadDir.resolve("toDelete.jpg")));
	}

	@Test
	void shouldThrowIfImageNotFoundOnRemove() {
		UUID id = UUID.randomUUID();
		when(repository.findById(id)).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> service.remove(id));
	}

	// ------------------------------------------------------------
	// TESTE 3: findByIdToDto()
	// ------------------------------------------------------------
	@Test
	void shouldFindImageAndReturnDto() {
		UUID id = UUID.randomUUID();
		Image image = Image.builder().id(id).filename("img.jpg").build();
		ImageDTO dto = new ImageDTO();

		when(repository.findById(id)).thenReturn(Optional.of(image));
		when(mapper.toDto(image)).thenReturn(dto);

		ImageDTO result = service.findByIdToDto(id);

		assertEquals(dto, result);
	}

	// ------------------------------------------------------------
	// TESTE 4: activeImage()
	// ------------------------------------------------------------
	@Test
	void shouldActivateImageSuccessfully() {
		Image image = new Image();
		image.setActive(false);

		when(repository.findById(any(UUID.class))).thenReturn(Optional.of(image));

		service.activeImage(UUID.randomUUID());

		assertTrue(image.getActive());
		verify(repository).save(image);
	}

	// ------------------------------------------------------------
	// TESTE 5: disabledImages()
	// ------------------------------------------------------------
	@Test
	void shouldDisableImagesSuccessfully() {
		Image img1 = new Image();
		img1.setActive(true);
		Image img2 = new Image();
		img2.setActive(true);

		List<Image> images = List.of(img1, img2);
		when(repository.findAllById(anyList())).thenReturn(images);

		service.disabledImages(List.of(UUID.randomUUID(), UUID.randomUUID()));

		verify(repository, times(2)).save(any(Image.class));
		assertFalse(img1.getActive());
		assertFalse(img2.getActive());
	}

	@Test
	void shouldDoNothingIfDisabledImagesListIsEmpty() {
		when(repository.findAllById(anyList())).thenReturn(Collections.emptyList());
		service.disabledImages(List.of(UUID.randomUUID()));
		verify(repository, never()).save(any());
	}
	
	@Test
	void shouldThrowRuntimeExceptionWhenIOExceptionOccurs() throws Exception {
	    MultipartFile file = mock(MultipartFile.class);
	    when(file.getOriginalFilename()).thenReturn("bad.jpg");
	    when(file.getInputStream()).thenThrow(new IOException("Simulated error"));

	    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.add(file, true));
	    assertTrue(ex.getMessage().contains("Erro ao salvar arquivo"));
	}

	
}
