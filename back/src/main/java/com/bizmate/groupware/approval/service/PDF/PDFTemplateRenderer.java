package com.bizmate.groupware.approval.service.PDF;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.document.DocumentType;
import com.bizmate.groupware.approval.repository.PDF.EmployeeSignatureRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFTemplateRenderer {

    private final EmployeeSignatureRepository employeeSignatureRepository;

    /**
     * ✅ 문서유형별 PDF 본문 생성
     */
    /**
     * ✅ 모든 문서유형 공통 PDF 렌더러
     * DB에 저장된 docContent(JSON)를 기반으로 자동 출력
     */
    public void renderByDocType(Document document, ApprovalDocuments doc) {
        DocumentType type = doc.getDocType();
        Map<String, Object> data = doc.getDocContent();

        document.add(new Paragraph("\n📄 문서유형: " + (type != null ? type.getLabel() : "미지정"))
                .setBold().setFontSize(14));
        document.add(new Paragraph(" "));

        if (data == null || data.isEmpty()) {
            document.add(new Paragraph("⚠️ 문서 내용이 없습니다."));
            return;
        }

        renderGeneric(document, data);
    }


    // ======================= 🧾 문서유형별 구현 =========================

    /**
     * ✅ Generic JSON → PDF 변환기
     * Map<String,Object> 데이터를 자동으로 표 형태로 렌더링
     */
    private void renderGeneric(Document doc, Map<String, Object> data) {
        // LinkedHashMap 으로 순서 유지 (DB 직렬화 순서 반영)
        Map<String, Object> orderedData = (data instanceof LinkedHashMap<String, Object>)
                ? data
                : new LinkedHashMap<>(data);

        List<String> excludeKeys = List.of(
                "_initialized", "initialized", "initFlag", "isInitialized"
        );

        Map<String, Object> filteredData = orderedData.entrySet().stream()
                .filter(e -> !excludeKeys.contains(e.getKey()))
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);

        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 75}))
                .useAllAvailableWidth()
                .setFontSize(11);

        for (Map.Entry<String, Object> entry : orderedData.entrySet()) {
            String key = normalizeKey(entry.getKey());
            Object val = entry.getValue();

            Cell keyCell = new Cell()
                    .add(new Paragraph("• " + key).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));

            Cell valCell = new Cell()
                    .add(new Paragraph(safe(val)))
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));

            table.addCell(keyCell);
            table.addCell(valCell);
        }

        doc.add(table);
    }

    /**
     * ✅ Key 명칭을 보기 좋게 정제 (예: camelCase → 한글형태)
     */
    private String normalizeKey(String key) {
        if (key == null) return "-";
        // 특정 키워드 자동 한글 매핑 (추가 가능)
        return switch (key) {
            // 🧾 공통 필드
            case "drafterName" -> "작성자";
            case "drafterDept" -> "소속 부서";
            case "createdDate" -> "작성일";
            case "_initialized" -> "초기화 여부";
            case "relatedDepts" -> "관련 부서";
            case "approvalLine" -> "결재선";
            case "docType" -> "문서유형";
            case "docTitle" -> "문서 제목";
            case "docStatus" -> "문서 상태";

            // 📄 품의서 (RequestForm)
            case "purpose" -> "기안 목적";
            case "details" -> "요청 사항";
            case "effect" -> "기대 효과";

            // 🧰 프로젝트 기획안 (ProjectPlanForm)
            case "projectName" -> "프로젝트명";
            case "goal" -> "목표";
            case "startDate" -> "시작일";
            case "endDate" -> "종료일";
            case "duration" -> "소요일수";
            case "participants" -> "참여 인원";
            case "summary" -> "프로젝트 개요";
            case "budgetItems" -> "예산 항목";
            case "totalBudget" -> "총 예산";
            case "effect2" -> "기대효과";

            // 💰 구매 품의서 (PurchaseForm)
            case "items" -> "구매 항목";
            case "budgetCategory" -> "예산 항목";
            case "reason" -> "구매 사유";
            case "unitPrice" -> "단가";
            case "qty" -> "수량";
            case "totalAmount" -> "총 합계 금액";

            // 🏖️ 휴가 신청서 (LeaveForm)
            case "leaveType" -> "휴가 유형";
            case "leaveDays" -> "총 휴가 일수";
            case "remainingLeave" -> "잔여 연차";
            case "reasonDetail" -> "휴가 사유";

            // 🧍‍♂️ 퇴직서 (ResignationForm)
            case "resignDate" -> "퇴사 예정일";
            case "handoverEmpId" -> "인수인계자";
            case "handoverDetails" -> "인수인계 내용";
            case "remark" -> "비고";

            // 👥 인사발령서 (HRMoveForm)
            case "targetEmpName" -> "발령 대상자";
            case "moveType" -> "발령 구분";
            case "effectiveDate" -> "발령일자";
            case "prevDept" -> "변경 전 부서";
            case "prevPosition" -> "변경 전 직책";
            case "newDeptName" -> "변경 후 부서";
            case "newPositionName" -> "변경 후 직책";
            case "note" -> "발령 사유";

            // 🧾 견적서 / 제안서 (EstimateProposal)
            case "clientName" -> "고객사명";
            case "clientManager" -> "담당자";
            case "proposalTitle" -> "제안서 제목";
            case "amount" -> "금액";

            // 💳 지출결의서 (ExpenseForm)
            case "category" -> "지출 항목";
            case "date" -> "지출 일자";
            case "paymentMethod" -> "결제 방법";
            case "desc" -> "세부 내역";

            // 🧩 기타 공통
            default -> prettifyKey(key);
        };
    }

    /**
     * ✅ 기본 fallback: camelCase → 단어 단위로 공백 삽입
     * 예: drafterName → Drafter Name
     */
    private String prettifyKey(String key) {
        if (key == null) return "-";
        String spaced = key.replaceAll("([A-Z])", " $1");
        return spaced.substring(0, 1).toUpperCase() + spaced.substring(1);
    }

    /**
     * ✅ null-safe 문자열 변환
     */
    private String safe(Object value) {

        if (value == null) return "-";

        // 🔹 1️⃣ String
        if (value instanceof String str) {
            // 날짜 포맷 자동 감지 (yyyy-MM-dd)
            if (str.matches("\\d{4}-\\d{2}-\\d{2}")) {
                try {
                    LocalDate date = LocalDate.parse(str);
                    return date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
                } catch (Exception e) {
                    return str;
                }
            }

            // 금액 문자열 자동 감지
            if (str.matches("^\\d{1,3}(,\\d{3})*$")) {
                return "₩" + str;
            }

            return str;
        }

        // 🔹 2️⃣ 숫자형 (Long, Integer, Double 등)
        if (value instanceof Number num) {
            return "₩" + String.format("%,d", num.longValue());
        }

        // 🔹 3️⃣ 날짜/시간 객체 자동 포맷
        if (value instanceof LocalDate date) {
            return date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
        }

        // 🔹 4️⃣ 리스트 → 줄바꿈 리스트
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(this::safe)
                    .map(s -> "• " + s)
                    .collect(Collectors.joining("\n"));
        }

        // 🔹 5️⃣ Map → key: value 형태 보기 좋게
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .map(e -> e.getKey() + ": " + safe(e.getValue()))
                    .collect(Collectors.joining(", "));
        }

        // 🔹 6️⃣ 기본 toString()
        return value.toString();
    }
}
