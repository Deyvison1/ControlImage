package com.controlimage.api.mapper;

import org.mapstruct.Mapper;
import com.controlimage.api.dto.ImageDTO;
import com.controlimage.api.model.Image;
import com.shareddtos.mapper.IBaseMapper;

@Mapper(componentModel = "spring")
public interface IImageMapper extends IBaseMapper<Image, ImageDTO> {
}