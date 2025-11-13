package com.controlimage.api.mapper;

import com.controlimage.api.dto.ImageDTO;
import com.controlimage.api.mapper.base.IBaseMapper;
import com.controlimage.api.model.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IImageMapper  extends IBaseMapper<Image, ImageDTO> {
}
