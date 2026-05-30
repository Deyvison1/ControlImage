package com.controlimage.api.service;

public interface StorageService {
	String upload(String bucket, String key, byte[] file, String contentType);
	void delete(String bucket, String key);
}
