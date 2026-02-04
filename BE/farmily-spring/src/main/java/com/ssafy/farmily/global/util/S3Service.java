package com.ssafy.farmily.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import com.ssafy.farmily.global.config.S3Properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * S3/MinIO 파일 업로드/다운로드 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param directory 저장할 디렉토리 (예: "diary", "profile", "timelapse")
     * @return 업로드된 파일의 URL
     */
    public String uploadFile(MultipartFile file, String directory) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String key = directory + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("파일 업로드 성공: {}", key);
            return buildFileUrl(key);
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * InputStream으로 파일 업로드
     * @param inputStream 파일 스트림
     * @param contentLength 파일 크기
     * @param contentType 콘텐츠 타입
     * @param directory 저장할 디렉토리
     * @param filename 파일 이름 (확장자 포함)
     * @return 업로드된 파일의 URL
     */
    public String uploadFile(InputStream inputStream, long contentLength, String contentType, String directory, String filename) {
        String key = directory + "/" + UUID.randomUUID() + "_" + filename;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, contentLength));

        log.info("파일 업로드 성공: {}", key);
        return buildFileUrl(key);
    }

    /**
     * 파일 삭제
     * @param fileUrl 삭제할 파일의 URL
     */
    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("파일 삭제 성공: {}", key);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 파일 존재 여부 확인
     * @param fileUrl 확인할 파일의 URL
     * @return 존재 여부
     */
    public boolean doesFileExist(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .build();

            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * 파일 URL 생성
     */
    private String buildFileUrl(String key) {
        return s3Properties.getEndpoint() + "/" + s3Properties.getBucketName() + "/" + key;
    }

    /**
     * URL에서 S3 key 추출
     */
    private String extractKeyFromUrl(String fileUrl) {
        String bucketPrefix = s3Properties.getEndpoint() + "/" + s3Properties.getBucketName() + "/";
        if (fileUrl.startsWith(bucketPrefix)) {
            return fileUrl.substring(bucketPrefix.length());
        }
        // URL에서 마지막 경로만 추출
        int lastSlash = fileUrl.lastIndexOf('/');
        return fileUrl.substring(lastSlash + 1);
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
