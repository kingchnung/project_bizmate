package com.bizmate.groupware.approval.api.PDF;

import com.bizmate.groupware.approval.service.PDF.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/approvals/pdf")
@RequiredArgsConstructor
public class ApprovalPDFController {

    private final PDFGeneratorService pdfGeneratorService;

    /**
     * ✅ 문서 PDF 생성 및 미리보기/다운로드
     */
    @GetMapping("/{docId}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable String docId) {
        byte[] pdfBytes = pdfGeneratorService.generateApprovalPdf(docId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(docId + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
