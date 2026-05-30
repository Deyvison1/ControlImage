package com.controlimage.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "storage.s3")
@Getter
@Setter
public class S3Properties {

	private String endpoint;
	private String accessKey;
	private String secretKey;
	private String bucket;
	private String region;
}
