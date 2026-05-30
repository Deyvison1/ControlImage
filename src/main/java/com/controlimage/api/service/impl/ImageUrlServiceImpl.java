package com.controlimage.api.service.impl;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.controlimage.api.service.ImageUrlService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class ImageUrlServiceImpl implements ImageUrlService {

	private final S3Presigner presigner;

	public String generate(String bucket, String key) {

		GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(10)).getObjectRequest(request).build();

		return presigner.presignGetObject(presignRequest).url().toString();
	}
}
