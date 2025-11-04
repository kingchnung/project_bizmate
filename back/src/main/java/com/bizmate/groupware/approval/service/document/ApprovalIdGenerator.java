package com.bizmate.groupware.approval.service.document;

import com.bizmate.groupware.approval.repository.document.ApprovalDocumentsRepository;
import com.bizmate.hr.domain.Department;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ApprovalIdGenerator
 * --------------------
 * ✅ 동시성 보장형 문서번호 생성기 (로컬/단일 서버용)
 * ✅ 서버 재시작 후에도 DB 기반으로 번호 이어받기
 * ✅ 날짜 변경 시 자동 리셋
 *
 * 형식: [부서코드]-[YYYYMMDD]-[001]
 * 예시: HR-20251011-003
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApprovalIdGenerator {

    private final com.bizmate.hr.repository.DepartmentRepository departmentRepository;
    private final ApprovalDocumentsRepository approvalDocumentsRepository;

    /** 날짜 포맷: 20251011 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    /** 일련번호 포맷: 3자리 */
    private static final String SEQUENCE_FORMAT = "%03d";
    /** 부서코드+날짜별 시퀀스 캐시 */
    private final Map<String, AtomicInteger> sequenceMap = new ConcurrentHashMap<>();

    /** 마지막 초기화된 날짜 (하루가 바뀌면 자동 리셋) */
    private LocalDate lastInitializedDate = LocalDate.now();

    // ✅ 1️⃣ 서버 기동 시 DB 데이터로 시퀀스 초기화
    @PostConstruct
    public void initializeSequenceFromDatabase() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DATE_FORMATTER);

        List<Department> departments = departmentRepository.findAll();
        log.info("🚀 서버 시작 - 문서번호 시퀀스 초기화 중...");

        for (Department dept : departments) {
            long count = approvalDocumentsRepository.countByDepartment_DeptIdAndCreatedAtBetween(
                    dept.getDeptId(),
                    today.atStartOfDay(),
                    today.plusDays(1).atStartOfDay()
            );
            String key = dept.getDeptCode() + "-" + todayStr;
            sequenceMap.put(key, new AtomicInteger((int) count));

            log.info("✅ [{}] 부서: 오늘 등록된 문서 {}건 → 시퀀스 {}번부터 시작", dept.getDeptCode(), count, count + 1);
        }

        lastInitializedDate = today;
        log.info("✅ 시퀀스 초기화 완료 ({})", todayStr);
    }

    /**
     * 새로운 문서번호 생성
     * @param departmentId 부서 ID
     * @param departmentCode 부서 코드 (예: HR, SL)
     * @return ex) HR-20251011-001
     */
    public synchronized String generateNewId(Long departmentId, String departmentCode) {
        if (departmentCode == null || departmentCode.isBlank()) {
            throw new IllegalArgumentException("부서코드는 비어 있을 수 없습니다.");
        }

        LocalDate today = LocalDate.now();
        String todayStr = today.format(DATE_FORMATTER);

        // 날짜 변경 시 캐시 초기화
        resetIfNewDay(today);

        // 캐시 키 = HR-20251011
        String key = departmentCode + "-" + todayStr;

        // 캐시에 없으면 (새 부서 추가 시) DB 기준 초기화
        AtomicInteger seq = sequenceMap.computeIfAbsent(key, k -> {
            long existing = approvalDocumentsRepository.countByDepartment_DeptIdAndCreatedAtBetween(
                    departmentId,
                    today.atStartOfDay(),
                    today.plusDays(1).atStartOfDay()
            );
            return new AtomicInteger((int) existing);
        });

        // 번호 증가
        int next = seq.incrementAndGet();

        String result = key + "-" + String.format(SEQUENCE_FORMAT, next);
        log.info("📄 생성된 문서번호: {}", result);
        return result;
    }

    /**
     * 날짜 변경 감지 시 자동 초기화
     */
    private void resetIfNewDay(LocalDate today) {
        if (!today.equals(lastInitializedDate)) {
            sequenceMap.clear();
            initializeSequenceFromDatabase();
            lastInitializedDate = today;
            log.info("🗓️ 날짜 변경 감지 → 시퀀스 전체 재초기화 완료 ({})", today);
        }
    }
}
