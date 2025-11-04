package com.bizmate.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${com.bizmate.upload.path}")
    private String uploadPath;

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));

            if (!resource.exists()) {
                // 파일 없으면 404 에러 반환
                return ResponseEntity.notFound().build();
            }

            // 파일의 MIME 타입 결정 (예: image/jpeg)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                // 타입을 알 수 없으면 기본값 사용
                contentType = "application/octet-stream";
            }

            // 다운로드 응답 생성
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    // Content-Disposition 헤더: 브라우저에게 파일 다운로드를 지시
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException ex) {
            // 파일 읽기 중 에러 발생 시 500 에러 반환
            // 실제 서비스에서는 로깅 추가 필요
            return ResponseEntity.internalServerError().build();
        }
    }
}