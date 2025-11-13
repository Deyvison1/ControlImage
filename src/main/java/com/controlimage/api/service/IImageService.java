package com.controlimage.api.service;

import com.controlimage.api.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IImageService {
    ImageDTO add(MultipartFile file, Boolean active);
    void remove(UUID id);
    ImageDTO findByIdToDto(UUID id);
    void activeImage(UUID id);
    void disabledImages(List<UUID> ids);
}
