package com.bizmate.groupware.approval.service.PDF;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.bizmate.groupware.approval.domain.document.Decision;
import com.bizmate.groupware.approval.repository.document.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.PDF.EmployeeSignatureRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PDFGeneratorService {

    private final ApprovalDocumentsRepository documentsRepository;
    private final EmployeeSignatureRepository employeeSignatureRepository;
    private final PDFTemplateRenderer pdfTemplateRenderer;

    private static final String PDF_PATH = "C:/bizmate/uploads/pdf/";



    /**
     * ✅ 결재문서 PDF 생성 (결재란 포함 / 결재자 수 자동 대응)
     */
    @Transactional(readOnly = true)
    public byte[] generateApprovalPdf(String docId) {
        ApprovalDocuments doc = documentsRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));


        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf);
        ) {
            // ✅ 한글 폰트 등록 (iText 7.2.4 완전 호환)
            String fontPath = getClass().getClassLoader()
                    .getResource("fonts/NotoSansKR-Regular.ttf")
                    .getPath();

            // iText 7.2.4에서는 이렇게 2단계로 로드해야 함
            PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H");
            document.setFont(font);

            // ✅ 헤더 + 결재란 (상단 배치)
            addHeaderWithApproval(document, doc, doc.getApprovalLine());

            // ✅ 문서정보
            addDocumentInfo(document, doc);

            // ✅ 문서 본문
            document.add(new Paragraph("\n"));
            pdfTemplateRenderer.renderByDocType(document, doc);

            // 푸터
            addFooter(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("❌ PDF 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    // ==============================================
    // 1️⃣ 헤더 + 우측 상단 결재란
    // ==============================================
    private void addHeaderWithApproval(Document document, ApprovalDocuments doc, List<ApproverStep> approvalLine) {

        // 문서 전체 여백 균일화
        document.setMargins(50f, 50f, 50f, 50f);

        // 2열 레이아웃: 왼쪽은 제목 + 문서정보, 오른쪽은 결재란
        Table layout = new Table(UnitValue.createPercentArray(new float[]{65, 35}))
                .useAllAvailableWidth()
                .setBorder(Border.NO_BORDER)
                .setMarginBottom(25);

        // --- [좌측 영역: BizMate] ---
        Cell left = new Cell().setBorder(Border.NO_BORDER);

        Paragraph title = new Paragraph("BizMate전자결재")
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15);
        left.add(title);
        layout.addCell(left);

        // --- [우측 영역: 결재란] ---
        Table approvalTable = createCompactApprovalTable(approvalLine);
        Cell right = new Cell()
                .add(approvalTable)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10) // 타이틀과 수평 맞춤
                .setPaddingLeft(15)
                .setPaddingRight(5);
        layout.addCell(right);

        document.add(layout);

        document.add(new Paragraph("\n\n"));
    }

    private void addDocumentInfo(Document document, ApprovalDocuments doc) {
        Table info = new Table(UnitValue.createPercentArray(new float[]{2, 5, 2, 5}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setMarginTop(5)
                .setMarginBottom(20);

        info.addCell(cell("문서유형", true));
        info.addCell(cell(doc.getDocType().getLabel(), false));
        info.addCell(cell("문서번호", true));
        info.addCell(cell(doc.getDocId(), false));

        info.addCell(cell("부서", true));
        info.addCell(cell(doc.getDepartment().getDeptName(), false));
        info.addCell(cell("작성자", true));
        info.addCell(cell(doc.getAuthorUser().getEmpName(), false));

        info.addCell(cell("작성일", true));
        info.addCell(cell(doc.getCreatedAt().toLocalDate().toString(), false));
        info.addCell(cell("사번", true));
        info.addCell(cell(doc.getAuthorUser().getEmployee().getEmpNo(), false));

        document.add(info);
    }

    /**
     * ✅ 결재자 수에 따라 동적으로 열 수를 조절하는 결재란
     */
    private Table createCompactApprovalTable(List<ApproverStep> approvalLine) {
        if (approvalLine == null || approvalLine.isEmpty()) {
            return new Table(1)
                    .addCell(new Cell().add(new Paragraph("결재선 정보 없음"))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(9));
        }

        // ✅ 실제 결재자 이름이 존재하는 사람만 필터링
        List<ApproverStep> validSteps = approvalLine.stream()
                .filter(step -> step.approverName() != null && !step.approverName().isBlank())
                .toList();

        int totalCols = validSteps.size(); // 👈 결재자 수만큼 열 생성

        Table table = new Table(UnitValue.createPercentArray(totalCols))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.8f))
                .setTextAlignment(TextAlignment.CENTER);

        // --------------------------------
        // [1] 결재자 이름 행
        // --------------------------------
        for (ApproverStep step : validSteps) {
            table.addCell(new Cell()
                    .add(new Paragraph(step.approverName()))
                    .setFontSize(9)
                    .setPadding(3)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 0.8f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }

        // --------------------------------
        // [2] 서명 이미지 행 (결재 완료자만 표시)
        // --------------------------------
        for (ApproverStep step : validSteps) {
            Cell cell = new Cell()
                    .setHeight(50)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 0.8f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            if (step.decision() == Decision.APPROVED) {
                String empId = step.approverId();
                File signFile = new File("src/main/resources/signatures/" + empId + ".png");

                if (signFile.exists()) {
                    try {
                        Image signImg = new Image(ImageDataFactory.create(signFile.toPath().toUri().toString()))
                                .setAutoScale(true)
                                .setHeight(40)
                                .setWidth(40)
                                .setHorizontalAlignment(HorizontalAlignment.CENTER);
                        cell.add(signImg);
                    } catch (Exception e) {
                        log.warn("⚠️ 서명 로드 실패: {}", signFile.getAbsolutePath(), e);
                        cell.add(new Paragraph("(서명 오류)").setFontSize(8));
                    }
                } else {
                    cell.add(new Paragraph(" ").setFontSize(8)); // ✅ 승인자이지만 파일 없음
                }
            } else {
                cell.add(new Paragraph(" ").setFontSize(8)); // ✅ 빈 서명란 (미결재)
            }

            table.addCell(cell);
        }

        // -------------------------------
        // [3] 결재 상태 (결재완료 / 반려 / 미결재)
        // -------------------------------
        for (ApproverStep step : validSteps) {
            String status = switch (step.decision()) {
                case APPROVED -> "결재완료";
                case REJECTED -> "반려";
                default -> "미결재";
            };
            table.addCell(new Cell()
                    .add(new Paragraph(status))
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 0.8f)));
        }

        return table;
    }

    private Cell cell(String text, boolean isHeader) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setFontSize(10)
                .setPadding(5)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        if (isHeader) cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return cell;
    }

    private void addFooter(Document document) {
        document.add(new Paragraph("\n\n본 문서는 BizMate 전자결재 시스템에서 자동 생성된 PDF입니다.")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));
    }
}
