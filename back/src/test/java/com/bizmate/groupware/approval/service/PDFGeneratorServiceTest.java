//package com.bizmate.groupware.approval.service;
//
//import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
//import com.bizmate.groupware.approval.repository.document.ApprovalDocumentsRepository;
//import com.bizmate.groupware.approval.repository.PDF.EmployeeSignatureRepository;
//import com.bizmate.groupware.approval.service.PDF.PDFGeneratorService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Slf4j
//@SpringBootTest
//public class PDFGeneratorServiceTest {
//
//    @Autowired
//    private PDFGeneratorService pdfGeneratorService;
//
//    @Autowired
//    private ApprovalDocumentsRepository documentsRepository;
//
//    @Autowired
//    private EmployeeSignatureRepository signatureRepository;
//
//
//    @Test
//    @Transactional
//    @DisplayName("✅ 실제 DB 문서(32-20251018-002)로 PDF 생성 테스트")
//    void generateRealDocumentPdf() throws Exception {
//        // given
//        String docId = "32-20251018-002";
//
//        // 문서 존재 여부 확인
//        ApprovalDocuments doc = documentsRepository.findById(docId)
//                .orElseThrow(() -> new IllegalStateException("❌ DB에 문서가 존재하지 않습니다: " + docId));
//
//        log.info("📄 테스트 문서: [{}] {} / 부서: {} / 작성자: {}",
//                doc.getDocId(),
//                doc.getTitle(),
//                doc.getDepartment() != null ? doc.getDepartment().getDeptName() : "N/A",
//                doc.getAuthorUser() != null ? doc.getAuthorUser().getEmpName() : "N/A"
//        );
//
//        // when
//        byte[] pdfBytes = pdfGeneratorService.generateApprovalPdf(docId);
//
//        // then
//        assertThat(pdfBytes).isNotNull();
//        assertThat(pdfBytes.length).isGreaterThan(0);
//
//        // ✅ 출력 디렉토리 생성
//        Path outputDir = Path.of("test-output");
//        if (!Files.exists(outputDir)) {
//            Files.createDirectories(outputDir);
//        }
//
//        // ✅ PDF 파일 생성
//        File outFile = new File(outputDir.toFile(), "결재문서_" + System.currentTimeMillis() + ".pdf");
//        try (FileOutputStream fos = new FileOutputStream(outFile)) {
//            fos.write(pdfBytes);
//        }
//
//        log.info("✅ PDF 생성 완료 → {}", outFile.getAbsolutePath());
//    }
//}
