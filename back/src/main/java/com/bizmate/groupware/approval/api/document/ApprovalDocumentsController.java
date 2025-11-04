package com.bizmate.groupware.approval.api.document;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.dto.approval.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.service.document.ApprovalDocumentsService;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalDocumentsController {

    private final ApprovalDocumentsService approvalDocumentsService;

    /* -------------------------------------------------------------
     ✅ 1️⃣ 결재문서 목록 조회 (페이징 + 공통 DTO 구조)
     ------------------------------------------------------------- */
    @GetMapping
    public ResponseEntity<PageResponseDTO<ApprovalDocumentsDto>> getApprovalList(
            @RequestParam(value = "status", required = false) String status,
            PageRequestDTO pageRequestDTO,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_CEO") ||
                                a.getAuthority().equals("ROLE_ADMIN")
                );

        log.info("📄 결재문서 목록 조회 요청: page={}, size={}, keyword={}, status={}, user={}, isAdmin={}",
                pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                pageRequestDTO.getKeyword(),
                status,
                principal.getUsername(),
                isAdmin
        );

        PageResponseDTO<ApprovalDocumentsDto> result;

        if (isAdmin) {
            // ✅ 관리자: 전체 문서 조회 (기존 로직 유지)
            if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
                result = approvalDocumentsService.getPagedApprovalsByStatus(pageRequestDTO, status);
            } else {
                result = approvalDocumentsService.getPagedApprovals(pageRequestDTO);
            }
        } else {
            // ✅ 일반 사용자: 작성자/열람자/결재자 포함 조회
            result = approvalDocumentsService.getPagedAccessibleDocuments(
                    pageRequestDTO,
                    principal.getUsername(),
                    status
            );
        }

        return ResponseEntity.ok(result);
    }

    /* -------------------------------------------------------------
     ✅ 2️⃣ 문서 상세 조회
     ------------------------------------------------------------- */
    @GetMapping("/{docId}")
    public ResponseEntity<ApprovalDocumentsDto> getDocumentDetail(@PathVariable String docId) {
        log.info("📋 문서 상세 조회: {}", docId);
        return ResponseEntity.ok(approvalDocumentsService.get(docId));
    }

    //위젯 조회
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getApprovalSummary(@AuthenticationPrincipal UserPrincipal principal) {
        String username = principal.getUsername();
        Map<String, Long> summary = approvalDocumentsService.getApprovalSummary(username);
        return ResponseEntity.ok(summary);
    }

    /* -------------------------------------------------------------
     ✅ 3️⃣ 문서 임시저장 (Draft)
     ------------------------------------------------------------- */
    @PostMapping("/draft")
    public ResponseEntity<ApprovalDocumentsDto> draftDocument(
            @RequestBody ApprovalDocumentsDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        try {
            log.info("💾 [임시저장 요청] {}", dto);

            // ✅ UserPrincipal → UserDTO 변환
            UserDTO loginUser = new UserDTO(
                    principal.getUserId(),
                    principal.getUsername(),
                    principal.getEmpName(),
                    principal.getEmail(),
                    principal.getEmpId()
            );

            dto.setUserId(loginUser.getUserId());
            dto.setAuthorName(loginUser.getEmpName());

            // ✅ 수정된 Service 호출 방식 (인자 2개)
            ApprovalDocumentsDto result = approvalDocumentsService.draft(dto, loginUser);

            return ResponseEntity.ok(result);

        } catch (VerificationFailedException e) {
            log.warn("🚫 임시저장 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 임시저장 중 예외 발생", e);
            throw new VerificationFailedException("임시저장 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
     ✅ 4️⃣ 문서 상신 (Submit)
     ------------------------------------------------------------- */
    @PostMapping("/submit")
    public ResponseEntity<ApprovalDocumentsDto> submitDocument(
            @RequestBody ApprovalDocumentsDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        try {
            log.info("🚀 [문서 상신 요청] {}", dto);

            // ✅ UserPrincipal → UserDTO 변환
            UserDTO loginUser = new UserDTO(
                    principal.getUserId(),
                    principal.getUsername(),
                    principal.getEmpName(),
                    principal.getEmail(),
                    principal.getEmpId()
            );

            // ✅ 작성자 정보 세팅 (표시용)
            dto.setUserId(loginUser.getUserId());
            dto.setAuthorName(loginUser.getEmpName());

            // ✅ 수정된 Service 호출
            ApprovalDocumentsDto result = approvalDocumentsService.submit(dto, loginUser);

            return ResponseEntity.ok(result);

        } catch (VerificationFailedException e) {
            log.warn("🚫 상신 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 상신 중 예외 발생", e);
            throw new VerificationFailedException("문서 상신 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
        재상신 (SUBMIT)
       ------------------------------------------------------------- */
    @PutMapping("/{docId}/resubmit")
    public ResponseEntity<ApprovalDocumentsDto> resubmitDocument(
            @PathVariable String docId,
            @RequestPart("data") ApprovalDocumentsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserPrincipal principal) {

        try {
            log.info("🔁 [문서 재상신 요청] 문서ID={}, 사용자(사번)={}", docId, principal.getUsername());

            // ✅ UserPrincipal → UserDTO 변환
            UserDTO loginUser = new UserDTO(
                    principal.getUserId(),
                    principal.getUsername(),
                    principal.getEmpName(),
                    principal.getEmail(),
                    principal.getEmpId()
            );

            // ✅ 작성자 정보 세팅 (표시용)
            dto.setUserId(loginUser.getUserId());
            dto.setAuthorName(loginUser.getEmpName());

            ApprovalDocumentsDto result = approvalDocumentsService.resubmit(docId, dto, files, loginUser);
            return ResponseEntity.ok(result);

        } catch (VerificationFailedException e) {
            log.warn("🚫 재상신 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 재상신 중 예외 발생", e);
            throw new VerificationFailedException("문서 재상신 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
 ✅ 5️⃣ 문서 승인 (Approve)
 ------------------------------------------------------------- */
    @PutMapping("/{docId}/approve")
    public ResponseEntity<ApprovalDocumentsDto> approveDocument(
            @PathVariable String docId,
            @AuthenticationPrincipal UserPrincipal principal) {

        try {
            boolean isAdmin = principal.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // ✅ UserPrincipal → UserDTO 변환
            UserDTO loginUser = new UserDTO(
                    principal.getUserId(),
                    principal.getUsername(),
                    principal.getEmpName(),
                    principal.getEmail(),
                    principal.getEmpId()
            );

            if (isAdmin) {
                log.info("✅ [관리자 강제 승인 실행] 관리자={}, 문서={}", loginUser.getEmpName(), docId);
            } else {
                log.info("✅ [문서 승인 요청] 승인자={}, 문서={}", loginUser.getEmpName(), docId);
            }

            ApprovalDocumentsDto result = approvalDocumentsService.approve(docId, loginUser);
            return ResponseEntity.ok(result);

        } catch (VerificationFailedException e) {
            log.warn("🚫 승인 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 승인 처리 중 예외 발생", e);
            throw new VerificationFailedException("문서 승인 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
     ✅ 6️⃣ 문서 반려 (Reject)
     ------------------------------------------------------------- */
    @PutMapping("/{docId}/reject")
    public ResponseEntity<ApprovalDocumentsDto> rejectDocument(
            @PathVariable String docId,
            @RequestBody(required = false) Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal) {

        try {
            // ✅ 1️⃣ reason 추출
            String reason = "";

            if (body != null && body.get("reason") != null) {
                reason = body.get("reason").toString();
            }

            boolean isAdmin = principal.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            UserDTO loginUser = new UserDTO(
                    principal.getUserId(),
                    principal.getUsername(),
                    principal.getEmpName(),
                    principal.getEmail(),
                    principal.getEmpId()
            );

            if (isAdmin) {
                log.info("🔴 [관리자 강제 반려] 문서ID={}, 관리자={}, 사유={}", docId, loginUser.getEmpName(), reason);
            } else {
                log.info("🔴 [반려 요청] 문서ID={}, 사용자={}, 사유={}", docId, loginUser.getEmpName(), reason);
            }

            ApprovalDocumentsDto result = approvalDocumentsService.reject(docId, loginUser, reason);
            return ResponseEntity.ok(result);

        } catch (VerificationFailedException e) {
            log.warn("🚫 반려 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 반려 처리 중 예외 발생", e);
            throw new VerificationFailedException("문서 반려 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
     ✅ 8️⃣ 문서 논리삭제 (관리자 전용)
     ------------------------------------------------------------- */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{docId}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable String docId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserDTO adminUser = new UserDTO(
                principal.getUserId(),
                principal.getUsername(),
                principal.getEmpName(),
                principal.getEmail(),
                principal.getEmpId()
        );

        log.info("🗑️ [관리자 문서 삭제] 문서ID={}, 관리자={}", docId, adminUser.getEmpName());

        approvalDocumentsService.logicalDelete(docId, adminUser, reason != null ? reason : "관리자 삭제");
        return ResponseEntity.ok(Map.of("message", "관리자에 의해 문서가 논리적으로 삭제되었습니다."));
    }

}
