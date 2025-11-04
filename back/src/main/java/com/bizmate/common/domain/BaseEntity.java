package com.bizmate.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 도메인 엔티티가 상속받는 기본 엔티티 클래스.
 * 데이터 생성 및 수정 시각과 작업자를 자동으로 기록(Auditing)합니다.
 * 이를 통해 모든 데이터에 대한 메타데이터 관리 및 추적이 용이해집니다.
 * ✅ BaseEntity
 * 모든 도메인 엔티티가 상속받는 공통 부모 클래스.
 * Auditing(Auto create/update tracking) + 도메인 내부 제어용 protected setter 제공.
 */

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter
@Slf4j
public abstract class BaseEntity {

    /** 생성 시각 */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 수정 시각 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 생성자  */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    /** 최종 작성자 */
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    /* ✅ 엔티티가 처음 저장될 때 로그 출력 */
    @PostPersist
    protected void onPostPersist() {
        log.info("📘 [Entity Created] {} | createdBy={} | createdAt={}",
                this.getClass().getSimpleName(), createdBy, createdAt);
    }

    /* ✅ 엔티티가 수정될 때 로그 출력 */
    @PostUpdate
    protected void onPostUpdate() {
        log.info("✏️ [Entity Updated] {} | updatedBy={} | updatedAt={}",
                this.getClass().getSimpleName(), updatedBy, updatedAt);
    }

    /* ✅ 도메인 내부(Aggregate Root 내부)에서만 접근 가능한 Setter */
    protected void setCreatedBy(String empName) { this.createdBy = empName; }
    protected void setUpdatedBy(String empName) { this.updatedBy = empName; }
    protected void setUpdatedAt(LocalDateTime time) { this.updatedAt = time; }
    protected void setCreatedAt(LocalDateTime time) { this.createdAt = time; }
}
