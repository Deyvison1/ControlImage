package com.controlimage.api.service.impl;

import org.springframework.stereotype.Service;

import com.controlimage.api.service.StorageService;

import lombok.AllArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {

	private final S3Client s3Client;

	@Override
	public String upload(String bucket, String key, byte[] file, String contentType) {

		try {
			s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType)
					.contentLength((long) file.length).build(), RequestBody.fromBytes(file));

			return key;

		} catch (S3Exception e) {
			throw new RuntimeException("Erro ao enviar arquivo para S3", e);
		}
	}

	@Override
	public void delete(String bucket, String key) {
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
	}
}