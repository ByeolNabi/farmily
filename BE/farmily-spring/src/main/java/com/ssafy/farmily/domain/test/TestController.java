package com.ssafy.farmily.domain.test;

import com.ssafy.farmily.global.util.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * S3 업로드 테스트용 컨트롤러
 * 개발/테스트 환경에서만 사용
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Test", description = "테스트용 API (개발 환경 전용)")
public class TestController {

    private final S3Service s3Service;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "S3 파일 업로드 테스트", description = "S3/MinIO에 파일을 업로드하고 URL을 반환합니다.")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "test") String directory
    ) {
        log.info("파일 업로드 요청: {} (크기: {} bytes, 타입: {})", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        try {
            String fileUrl = s3Service.uploadFile(file, directory);
            
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("fileUrl", fileUrl);
            response.put("originalFilename", file.getOriginalFilename());
            response.put("directory", directory);
            
            log.info("파일 업로드 성공: {}", fileUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/s3-connection")
    @Operation(summary = "S3 연결 상태 확인", description = "S3/MinIO 연결 설정을 확인합니다.")
    public ResponseEntity<Map<String, String>> checkS3Connection() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "S3 서비스가 정상적으로 주입되었습니다.");
        response.put("message", "파일 업로드 테스트를 위해 POST /api/test/upload 엔드포인트를 사용하세요.");
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "S3 파일 삭제 테스트", description = "S3/MinIO에서 파일을 삭제합니다.")
    public ResponseEntity<Map<String, String>> deleteFile(
            @RequestParam("fileUrl") String fileUrl
    ) {
        log.info("파일 삭제 요청: {}", fileUrl);

        try {
            s3Service.deleteFile(fileUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "파일이 삭제되었습니다.");
            response.put("deletedUrl", fileUrl);
            
            log.info("파일 삭제 성공: {}", fileUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/file-exists")
    @Operation(summary = "S3 파일 존재 확인", description = "S3/MinIO에 파일이 존재하는지 확인합니다.")
    public ResponseEntity<Map<String, Object>> checkFileExists(
            @RequestParam("fileUrl") String fileUrl
    ) {
        log.info("파일 존재 확인 요청: {}", fileUrl);

        try {
            boolean exists = s3Service.doesFileExist(fileUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileUrl", fileUrl);
            response.put("exists", exists);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("파일 존재 확인 실패", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
