package com.bizmate.salesPages.management.collections.service;

import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.repository.ClientRepository;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.management.collections.domain.Collection;
import com.bizmate.salesPages.management.collections.dto.CollectionDTO;
import com.bizmate.salesPages.management.collections.repository.CollectionRepository;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.report.salesReport.dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional
public class CollectionServiceImpl implements CollectionService{
    private final CollectionRepository collectionRepository;
    private final ClientRepository clientRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Entity(Collection)를 DTO(CollectionDTO)로 변환하는 헬퍼 메서드.
     */
    private CollectionDTO convertToDTO(Collection collection) {
        // Client가 LAZY 로딩될 수 있으므로, .getClient() 호출 시 초기화
        Client client = collection.getClient();

        return CollectionDTO.builder()
                .collectionNo(collection.getCollectionNo())
                .collectionId(collection.getCollectionId())
                .collectionDate(collection.getCollectionDate())
                .collectionMoney(collection.getCollectionMoney())
                .collectionNote(collection.getCollectionNote())
                .userId(collection.getUserId())
                .writer(collection.getWriter())
                .clientId(client.getClientId())
                .clientCompany(client.getClientCompany())
                .build();
    }

    @Override
    public String register(CollectionDTO collectionDTO) {
        LocalDate today = LocalDate.now();

        // 1. DTO에서 전달받은 clientId로 실제 Client 엔티티를 DB에서 조회
        String clientId = collectionDTO.getClientId();
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client with ID " + clientId + " not found."));

        collectionDTO.setClientCompany(client.getClientCompany());

        String maxCollectionId = collectionRepository.findMaxCollectionIdByCollectionDate(today).orElse(null);

        int nextSequence = 1;
        if(maxCollectionId != null) {
            try {
                String seqStr = maxCollectionId.substring(9);
                nextSequence = Integer.parseInt(seqStr) + 1;
            } catch (Exception e) {
                nextSequence = 1;
            }
        }

        String datePart = today.format(DATE_FORMAT);
        String sequencePart = String.format("%04d", nextSequence);
        String finalCollectionId = datePart + "-" + sequencePart;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof UserPrincipal userPrincipal){
            collectionDTO.setUserId(userPrincipal.getUsername());
            collectionDTO.setWriter(userPrincipal.getEmpName());
        } else {
            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
        }

        // 3. Collection 엔티티를 수동으로 Builder를 사용하여 생성
        // ModelMapper의 충돌을 방지하고, DB에서 조회한 Client 엔티티를 직접 주입
        Collection collection = Collection.builder()
                .collectionId(finalCollectionId)
                .collectionMoney(collectionDTO.getCollectionMoney())
                .collectionNote(collectionDTO.getCollectionNote())
                .writer(collectionDTO.getWriter())
                .userId(collectionDTO.getUserId())
                .client(client)
                .build();

        Collection savedCollection = collectionRepository.save(collection);
        return savedCollection.getCollectionId();
    }

    @Override
    public CollectionDTO get(String collectionId) {
        Optional<Collection> result = collectionRepository.findByCollectionId(collectionId);
        Collection collection = result.orElseThrow(() -> new NoSuchElementException("Collection not found for ID: " + collectionId));

        CollectionDTO dto = convertToDTO(collection);
        return dto;
    }

    @Override
    public void modify(CollectionDTO collectionDTO) {
        Optional<Collection> result = collectionRepository.findByCollectionId(collectionDTO.getCollectionId());
        Collection collection = result.orElseThrow(() -> new NoSuchElementException("Collection not found for ID: " + collectionDTO.getCollectionId()));

        collection.changeCollectionDate(collectionDTO.getCollectionDate());
        collection.changeCollectionMoney(collectionDTO.getCollectionMoney());
        collection.changeCollectionNote(collectionDTO.getCollectionNote());

        if(!collection.getClient().getClientId().equals(collectionDTO.getClientId())) {
            String newClientId = collectionDTO.getClientId();
            Client newClient = clientRepository.findByClientId(newClientId).orElseThrow(() -> new NoSuchElementException("New Client with ID " + newClientId + " not found."));
            collection.changeClient(newClient);
        }

        collectionRepository.save(collection);
    }

    @Override
    public void remove(String collectionId) {
        collectionRepository.deleteByCollectionId(collectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CollectionDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("collectionId").descending());

        Page<Collection> result = collectionRepository.searchCollection(pageRequestDTO, pageable);

        // ModelMapper 대신 수동 헬퍼 메서드 사용
        List<CollectionDTO> dtoList = result.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<CollectionDTO> responseDTO = PageResponseDTO.<CollectionDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionDTO> listByClient(String clientId) {
        // 💡 DTO 변환은 기존 convertToDTO 헬퍼 메소드 재사용
        return collectionRepository.findByClient_ClientIdOrderByCollectionDateDesc(clientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
