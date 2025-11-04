package com.bizmate.groupware.approval.service.PDF;

import com.bizmate.hr.domain.Employee;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFSignatureService {

    private final EmployeeSignatureService employeeSignatureService;

    /**
     * 지정한 직원의 서명 이미지를 반환
     */
    /**
     * ✅ 결재란 셀에 서명 이미지 추가
     */
    public void addSignToCell(Cell cell, Employee employee) {
        try {
            String path = employeeSignatureService.getSignaturePath(employee);
            if (path == null) {
                cell.add(new Paragraph("서명 없음").setTextAlignment(TextAlignment.CENTER));
                return;
            }

            File file = new File(path);
            ImageData imageData = ImageDataFactory.create(file.getAbsolutePath());
            Image image = new Image(imageData)
                    .setAutoScale(true)
                    .scaleToFit(60, 40)
                    .setTextAlignment(TextAlignment.CENTER);
            cell.add(image);

            log.info("🖋️ 서명 이미지 삽입 완료: {}", file.getName());
        } catch (Exception e) {
            log.error("❌ 서명 이미지 추가 실패: {}", e.getMessage(), e);
            cell.add(new Paragraph("오류").setTextAlignment(TextAlignment.CENTER));
        }
    }

    /**
     * ✅ 문서 객체(Document)에 직접 서명 추가 (예: 좌표 기반)
     */
    public void addSignToDocument(Document document, Employee employee, float x, float y) {
        try {
            String path = employeeSignatureService.getSignaturePath(employee);
            if (path == null) {
                log.warn("⚠️ 서명 이미지 없음 (사번={}): PDF에 삽입 생략", employee.getEmpNo());
                return;
            }

            ImageData imageData = ImageDataFactory.create(path);
            Image image = new Image(imageData)
                    .scaleToFit(80, 50)
                    .setFixedPosition(x, y);
            document.add(image);

            log.info("✅ 문서 내 서명 추가 완료: {}", employee.getEmpNo());
        } catch (Exception e) {
            log.error("❌ 문서 서명 삽입 실패: {}", e.getMessage(), e);
        }
    }
}


