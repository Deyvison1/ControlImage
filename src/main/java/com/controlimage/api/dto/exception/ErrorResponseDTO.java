package com.controlimage.api.dto.exception;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDTO {
	private final Instant timestamp;
	private final int status;
	private final String error;
	private final String message;
	private final String path;
}
