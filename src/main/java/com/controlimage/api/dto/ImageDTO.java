package com.controlimage.api.dto;

import java.io.Serial;

import com.shareddtos.dto.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO extends BaseDTO {

	@Serial
	private static final long serialVersionUID = 1L;

	private String filename;
	private String bucket;
	private String objectKey;
	private String contentType;
	private String url;
	private Long size;
	private Boolean active;
}