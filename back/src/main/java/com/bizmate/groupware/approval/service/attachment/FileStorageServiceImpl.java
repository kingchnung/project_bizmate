package com.bizmate.groupware.approval.service.attachment;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.attachment.ApprovalFileAttachment;
import com.bizmate.groupware.approval.repository.attachment.ApprovalFileAttachmentRepository;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final ApprovalFileAttachmentRepository fileAttachmentRepository;

    // ✅ OS별 경로 맞게 변경 가능
    private static final String UPLOAD_DIR = "C:/bizmate/uploads";

    @Override
    public ApprovalFileAttachment saveFile(MultipartFile file, ApprovalDocuments document, UserDTO uploader) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("파일이 비어 있습니다.");
            }

            // ✅ 디렉토리 없으면 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // ✅ 원본 이름 & 확장자
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // ✅ 저장 파일명
            String storedName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(storedName);

            // ✅ 실제 파일 저장
            Files.copy(file.getInputStream(), filePath);

            // ✅ Content Type 추론
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = Files.probeContentType(filePath);
                if (contentType == null) contentType = "application/octet-stream";
            }

            UserEntity uploaderEntity = UserEntity.builder()
                    .userId(uploader.getUserId())
                    .username(uploader.getUsername())
                    .empName(uploader.getEmpName())
                    .deptName(uploader.getDeptName())
                    .deptCode(uploader.getDeptCode())
                    .build();

            // ✅ DB 저장
            ApprovalFileAttachment attachment = ApprovalFileAttachment.builder()
                    .document(document)
                    .originalName(originalName)
                    .storedName(storedName)
                    .filePath(filePath.toString())
                    .fileSize(file.getSize())
                    .contentType(contentType)
                    .uploadedAt(LocalDateTime.now())
                    .uploader(uploaderEntity)
                    .build();

            ApprovalFileAttachment saved = fileAttachmentRepository.save(attachment);
            log.info("✅ 파일 저장 완료: {} ({} bytes, type={})", originalName, file.getSize(), contentType);
            return saved;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 파일 삭제 메서드
     * - 실제 파일 삭제
     * - 존재하지 않아도 예외 없이 통과
     */
    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;

        try {
            Path path = Paths.get(filePath);
            boolean deleted = Files.deleteIfExists(path);

            if (deleted) {
                log.info("🗑️ 파일 삭제 완료: {}", filePath);
            } else {
                log.warn("⚠️ 삭제 대상 파일이 존재하지 않음: {}", filePath);
            }
        } catch (Exception e) {
            log.warn("⚠️ 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
