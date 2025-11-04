package com.bizmate.salesPages.client.repository;

import com.bizmate.salesPages.client.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.envers.repository.support.EnversRevisionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long>, EnversRevisionRepository<Client, Long, Integer> {
    Client findByClientNo(Long clientNo);
    Optional<Client> findByClientId(String clientId);

    @Query(value = "SELECT c FROM Client c ORDER BY NLSSORT(c.clientCompany, 'NLS_SORT=KOREAN') DESC")
    List<Client> findByOrderByClientCompanyDesc();

    Page<Client> findAll(Pageable pageable);

    Page<Client> findByClientIdContaining(String clientId, Pageable pageable);
    Page<Client> findByClientCompanyContaining(String clientCompany, Pageable pageable);
    Page<Client> findByClientCeoContaining(String clientCeo, Pageable pageable);
    Page<Client> findByClientContactContaining(String clientContact, Pageable pageable);
    Page<Client> findByUserIdContaining(String userId, Pageable pageable);

}
