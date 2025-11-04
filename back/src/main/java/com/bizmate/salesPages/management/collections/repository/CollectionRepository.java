package com.bizmate.salesPages.management.collections.repository;

import com.bizmate.salesPages.management.collections.domain.Collection;
import com.bizmate.salesPages.report.salesReport.dto.CollectionSummary;
import org.springframework.data.envers.repository.support.EnversRevisionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long>, CollectionRepositoryCustom, EnversRevisionRepository<Collection, Long, Integer> {

    @Query("SELECT MAX(c.collectionId) FROM Collection c WHERE c.collectionDate = :collectionDate")
    Optional<String> findMaxCollectionIdByCollectionDate(@Param("collectionDate")LocalDate today);

    @Query("SELECT c.collectionId FROM Collection c ORDER BY c.collectionId ASC LIMIT 1")
    List<String> findMinCollectionId();

    // String 타입 collectionId로 엔티티를 조회하는 메서드 추가
    Optional<Collection> findByCollectionId(String collectionId);

    // String 타입 collectionId로 엔티티를 삭제하는 메서드 추가
    @Modifying
    @Query("DELETE FROM Collection c WHERE c.collectionId = :collectionId")
    void deleteByCollectionId(@Param("collectionId") String collectionId);

    @Query("""
        SELECT new com.bizmate.salesPages.report.salesReport.dto.CollectionSummary(
             c.client.clientId, c.client.clientCompany, SUM(c.collectionMoney)
        ) 
        FROM Collection c 
        GROUP BY c.client.clientId, c.client.clientCompany 
        ORDER BY SUM(c.collectionMoney) DESC
        """
    )
    List<CollectionSummary> findTotalCollectionAmountGroupByClient();

    List<Collection> findByClient_ClientIdOrderByCollectionDateDesc(String clientId);

}

