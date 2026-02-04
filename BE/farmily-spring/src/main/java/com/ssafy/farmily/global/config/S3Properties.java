package com.ssafy.farmily.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * S3/MinIO 속성 홀더
 */
@Component
@Getter
public class S3Properties {

    @Value("${farmily.s3.endpoint}")
    private String endpoint;

    @Value("${farmily.s3.region}")
    private String region;

    @Value("${farmily.s3.access-key}")
    private String accessKey;

    @Value("${farmily.s3.secret-key}")
    private String secretKey;

    @Value("${farmily.s3.bucket-name}")
    private String bucketName;
}
