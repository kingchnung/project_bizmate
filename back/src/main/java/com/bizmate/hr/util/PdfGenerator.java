package com.bizmate.hr.util;

import com.bizmate.hr.domain.Employee;

import com.itextpdf.text.Document;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;


@Component
@Slf4j
public class PdfGenerator {

    public byte[] createEmploymentCertificate(Employee emp) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            //문서객체생성
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            // [수정 후]
            // 1. resources/fonts/malgun.ttf 경로의 폰트 리소스를 가져옵니다.
            ClassPathResource resource = new ClassPathResource("fonts/NotoSansKR-Regular.ttf");

            // 2. 폰트 리소스를 byte 배열로 읽어옵니다.
            byte[] fontBytes;
            try (InputStream is = resource.getInputStream()) {
                fontBytes = is.readAllBytes(); // Java 9+
            }

            // 3. 폰트 바이트 배열을 사용하여 BaseFont 객체 생성
            // "malgun.ttf"는 폰트 식별 이름이며, 마지막 인자(pfb)는 null로 설정합니다.
            BaseFont baseFont = BaseFont.createFont(
                    "malgun.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false, // (cached)
                    fontBytes, // (ttfAfm)
                    null // (pfb)
            );

            Font titleFont = new Font(baseFont, 20, Font.BOLD);
            Font bodyFont = new Font(baseFont, 12);

            Paragraph title = new Paragraph("재직 증명서", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);


            document.add(new Paragraph("\n\n이하 직원의 재직사항을 증명합니다.\n\n", bodyFont));

            document.add(new Paragraph("성명 : " + safe(emp.getEmpName()), bodyFont));
            document.add(new Paragraph("부서 : " + safe(emp.getDepartment().getDeptName()), bodyFont));
            document.add(new Paragraph("직책 : " + safe(emp.getPosition().getPositionName()), bodyFont));
            document.add(new Paragraph("입사일 : " + safe(emp.getStartDate() != null ? emp.getStartDate().toString() : null), bodyFont));
            document.add(new Paragraph("이메일 : " + safe(emp.getEmail()), bodyFont));


            document.add(new Paragraph("\n\n발급일: " + LocalDate.now(), bodyFont));
            document.add(new Paragraph("\n\n주식회사 BizMate 대표이사 귀하", bodyFont));

            Paragraph footer = new Paragraph("\n\n(인)", bodyFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();

            log.info("✅ 재직증명서 PDF 생성 완료: {}", emp.getEmpName());
            return out.toByteArray();

        } catch (Exception e) {
            log.error("❌ PDF 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("PDF 생성 중 오류 발생", e);
        }
    }
    // null-safe 문자열 출력 */
    private String safe(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }
}
