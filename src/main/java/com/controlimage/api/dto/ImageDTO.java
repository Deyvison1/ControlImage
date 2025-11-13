package com.controlimage.api.dto;

import com.controlimage.api.dto.base.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO extends BaseDTO {
    @Serial
    private static final long serialVersionUID = 1L;
    private String filename;
    private String url;
    private String contentType;
    private Long size;
    private Boolean active;
}
