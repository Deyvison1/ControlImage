package com.controlimage.api.mapper.base;

import com.controlimage.api.dto.base.BaseDTO;
import com.controlimage.api.model.base.BaseEntity;

public interface IBaseMapper<E extends BaseEntity, D extends BaseDTO> {
    D toDto(E entity);

    E toEntity(D dto);
}
