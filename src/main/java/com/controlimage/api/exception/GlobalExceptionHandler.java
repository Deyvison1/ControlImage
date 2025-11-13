package com.controlimage.api.exception;

import java.nio.file.FileAlreadyExistsException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.controlimage.api.dto.exception.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Captura RuntimeException genéricas (500)
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseDTO> handleRuntime(RuntimeException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		if (status == null) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return buildResponse(status, ex.getReason(), request.getRequestURI());
	}

	// Captura FileAlreadyExistsException (409 Conflict)
	@ExceptionHandler(FileAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDTO> handleFileAlreadyExists(FileAlreadyExistsException ex,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
	}

	// Método auxiliar para construir o DTO padronizado
	private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message, String path) {
		ErrorResponseDTO error = new ErrorResponseDTO(Instant.now(), status.value(), status.getReasonPhrase(), message,
				path);
		return ResponseEntity.status(status).body(error);
	}
}
