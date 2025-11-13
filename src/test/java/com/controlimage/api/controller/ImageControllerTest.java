package com.controlimage.api.controller;

import com.controlimage.api.dto.ImageDTO;
import com.controlimage.api.service.IImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ImageControllerTest {

	private MockMvc mockMvc;

	@Mock
	private IImageService service;

	@InjectMocks
	private ImageController controller;

	private ImageDTO imageDTO;
	private UUID id;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		id = UUID.randomUUID();

		imageDTO = new ImageDTO();
		imageDTO.setId(id);
		imageDTO.setFilename("test.jpg");
		imageDTO.setUrl("/images/test.jpg");
		imageDTO.setContentType("image/jpeg");
	}

	@Test
	void shouldUploadImageSuccessfully() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
		when(service.add(any(), anyBoolean())).thenReturn(imageDTO);

		mockMvc.perform(
				multipart("/api/image").file(file).param("active", "true").contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.filename").value("test.jpg"));

		verify(service).add(any(), eq(true));
	}

	@Test
	void shouldReturnImageById() throws Exception {
		when(service.findByIdToDto(id)).thenReturn(imageDTO);

		mockMvc.perform(get("/api/image/{id}", id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id.toString())).andExpect(jsonPath("$.filename").value("test.jpg"));

		verify(service).findByIdToDto(id);
	}

	@Test
	void shouldDownloadImageSuccessfully() throws Exception {
		when(service.findByIdToDto(id)).thenReturn(imageDTO);

		Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
		Files.createDirectories(uploadDir);
		Path filePath = uploadDir.resolve("test.jpg");
		Files.write(filePath, "data".getBytes());

		mockMvc.perform(get("/api/image/{id}/download", id)).andExpect(status().isOk())
				.andExpect(header().string("Content-Type", "image/jpeg"));

		Files.deleteIfExists(filePath);
	}

	@Test
	void shouldDeleteImageSuccessfully() throws Exception {
		doNothing().when(service).remove(id);

		mockMvc.perform(delete("/api/image/{id}", id)).andExpect(status().isOk());

		verify(service).remove(id);
	}

	@Test
	void shouldActivateImageSuccessfully() throws Exception {
		doNothing().when(service).activeImage(id);

		mockMvc.perform(put("/api/image/{id}/activate", id)).andExpect(status().isOk());

		verify(service).activeImage(id);
	}

	@Test
	void shouldDisableImagesSuccessfully() throws Exception {
	    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
	    doNothing().when(service).disabledImages(ids);

	    String joinedIds = String.join(",", ids.stream().map(UUID::toString).toList());

	    mockMvc.perform(put("/api/image/{ids}/disabled", joinedIds))
	            .andExpect(status().isOk());

	    verify(service).disabledImages(ids);
	}
}
