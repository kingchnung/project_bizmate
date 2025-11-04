package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.document.Decision;
import com.bizmate.groupware.approval.domain.document.DocumentStatus;
import com.bizmate.groupware.approval.domain.document.DocumentType;
import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.bizmate.groupware.approval.dto.approval.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.notification.NotificationService;
import com.bizmate.groupware.approval.repository.document.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.attachment.ApprovalFileAttachmentRepository;
import com.bizmate.groupware.approval.repository.PDF.EmployeeSignatureRepository;
import com.bizmate.groupware.approval.service.document.ApprovalDocumentsServiceImpl;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.UserRepository;

import com.bizmate.project.dto.project.ProjectRequestDTO;
import com.bizmate.project.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("✅ ApprovalService - 프로젝트 자동 생성 테스트")
@Transactional
class ApprovalDocumentsServiceImplTests {

    @Mock
    private ApprovalDocumentsRepository approvalDocumentsRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeeSignatureRepository employeeSignatureRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private ProjectService projectService;
    @Mock private ObjectMapper objectMapper;
    @Mock
    private ApprovalFileAttachmentRepository approvalFileAttachmentRepository;

    @InjectMocks
    private ApprovalDocumentsServiceImpl approvalService;

    private ApprovalDocuments document;
    private UserDTO approver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 🔹 결재선 구성
        List<ApproverStep> approvalLine = new ArrayList<>();
        approvalLine.add(new ApproverStep(1, "emp001", "홍길동", Decision.PENDING, "", null, null));

        // 🔹 문서 생성 (PROJECT_PLAN)
        document = ApprovalDocuments.builder()
                .docId("PRJ-20251021-001")
                .title("스마트팩토리 구축 기획안")
                .docType(DocumentType.PROJECT_PLAN)
                .status(DocumentStatus.IN_PROGRESS)
                .approvalLine(approvalLine)
                .currentApproverIndex(0)
                .docContent(Map.of(
                        "projectName", "스마트팩토리 구축",
                        "goal", "생산 자동화",
                        "budgetItems", List.of(Map.of("항목", "설비비", "금액", 3000000))
                ))
                .authorUser(
                        new com.bizmate.hr.domain.UserEntity().builder()
                                .username("writer")
                                .email("writer@bizmate.com")
                                .build()
                )
                .build();

        // 🔹 승인자 UserDTO
        approver = new UserDTO(
                1L, "hong", "홍길동", "hong@bizmate.com", 1001L
        );
        approver.setDeptName("생산기술팀");
    }

    @Test
    @DisplayName("🧩 PROJECT_PLAN 문서 승인 시 ProjectService.createProject 자동 호출")
    void testProjectAutoCreateAfterFinalApproval() {
        // given
        when(approvalDocumentsRepository.findById("PRJ-20251021-001"))
                .thenReturn(Optional.of(document));
        when(employeeRepository.findByEmpId(1001L))
                .thenReturn(Optional.of(Employee.builder().empId(1001L).empName("홍길동").build()));
        when(employeeSignatureRepository.findByEmployee(any()))
                .thenReturn(Optional.empty());

        // 문서 내용 → ProjectRequestDTO 변환 mock
        when(objectMapper.convertValue(any(), eq(ProjectRequestDTO.class)))
                .thenReturn(ProjectRequestDTO.builder()
                        .projectName("스마트팩토리 구축")
                        .build());

        // when
        ApprovalDocumentsDto result = approvalService.approve("PRJ-20251021-001", approver);

        // then
        assertThat(result.getStatus()).isEqualTo(DocumentStatus.APPROVED.name());
        verify(projectService, times(1))
                .createProjectByApproval(any(ProjectRequestDTO.class), eq(document));

        verify(approvalDocumentsRepository, times(1)).saveAndFlush(document);
        verify(notificationService, atLeastOnce())
                .sendApprovalCompleteMail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("⚠️ PROJECT_PLAN 이 아닌 문서는 ProjectService 호출되지 않는다")
    void testNonProjectDocNotCreateProject() {
        // given
        document.setDocType(DocumentType.EXPENSE); // 지출결의서
        when(approvalDocumentsRepository.findById("PRJ-20251021-001"))
                .thenReturn(Optional.of(document));
        when(employeeRepository.findByEmpId(1001L))
                .thenReturn(Optional.of(Employee.builder().empId(1001L).empName("홍길동").build()));

        // when
        ApprovalDocumentsDto result = approvalService.approve("PRJ-20251021-001", approver);

        // then
        assertThat(result.getStatus()).isEqualTo(DocumentStatus.APPROVED.name());
        verify(projectService, never()).createProjectByApproval(any(), any());
    }
}