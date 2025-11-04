package com.bizmate.hr.repository;

import com.bizmate.hr.domain.code.Position; // package 변경 가정
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByPositionName(String positionName); // ★ 메서드명 변경

    Optional<Position> findByPositionCode(Long positionCode);
}