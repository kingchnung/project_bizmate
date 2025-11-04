package com.bizmate.common.audit;

import com.bizmate.common.audit.RevInfo;
import com.bizmate.common.audit.AuditHistoryDto;
import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.repository.ClientRepository;
import com.bizmate.salesPages.management.collections.domain.Collection;
import com.bizmate.salesPages.management.collections.repository.CollectionRepository;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import com.bizmate.salesPages.report.salesTarget.repository.SalesTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.data.history.Revision;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class AuditHistoryController {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;
    private final SalesRepository salesRepository;
    private final CollectionRepository collectionRepository;
    private final SalesTargetRepository salesTargetRepository;

    private static String mapType(RevisionMetadata.RevisionType t) {
        return switch (t) {
            case INSERT -> "ADD";
            case UPDATE -> "MOD";
            case DELETE -> "DEL";
            default -> "MOD"; // null 포함 모든 나머지
        };
    }

    private List<AuditHistoryDto> mapRevisionsToDtoList(Revisions<Integer, ?> revisions) {
        return revisions.getContent().stream()
                .map((Revision<Integer, ?> r) -> {
                    // 리비전 메타
                    var meta = r.getMetadata();

                    // 타임스탬프 변환 (존 안전)
                    LocalDateTime ts = meta.getRevisionInstant()
                            .map(instant -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
                            .orElse(null);

                    // 커스텀 리비전 엔티티
                    String modifierId = "unknown";
                    String modifierName = "unknown";
                    String modifierFull = "unknown";

                    Object delegate = meta.getDelegate();
                    if (delegate instanceof RevInfo rev) {
                        modifierId = rev.getModifierId();
                        modifierName = rev.getModifierName();
                        modifierFull = rev.getModifierFull();
                    }

                    Integer revNum = meta.getRevisionNumber().orElse(0);
                    String type = meta.getRevisionType() != null ? mapType(meta.getRevisionType()) : "MOD";

                    return AuditHistoryDto.builder()
                            .revisionId(revNum.longValue())
                            .revisionTimestamp(ts)
                            .revisionType(type)
                            .modifierId(modifierId)
                            .modifierName(modifierName)
                            .modifierFull(modifierFull)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/client/{id}")       public ResponseEntity<List<AuditHistoryDto>> getClientHistory(@PathVariable Long id) { return ResponseEntity.ok(mapRevisionsToDtoList(clientRepository.findRevisions(id))); }

    // String ID로 Order를 찾아 Long PK(orderNo)를 Renvision에 전달
    @GetMapping("/order/{id}")
    public ResponseEntity<List<AuditHistoryDto>> getOrderHistory(@PathVariable String id) {
        Order order = orderRepository.findByOrderId(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + id));
        return ResponseEntity.ok(mapRevisionsToDtoList(orderRepository.findRevisions(order.getOrderNo())));
    }

    @GetMapping("/sales/{id}")
    public ResponseEntity<List<AuditHistoryDto>> getSalesHistory(@PathVariable String id) {
        Sales sales = salesRepository.findBySalesId(id)
                .orElseThrow(() -> new NoSuchElementException("Sales not found with ID: " + id));
        return ResponseEntity.ok(mapRevisionsToDtoList(salesRepository.findRevisions(sales.getSalesNo())));
    }

    @GetMapping("/collection/{id}")
    public ResponseEntity<List<AuditHistoryDto>> getCollectionHistory(@PathVariable String id) {
        Collection collection = collectionRepository.findByCollectionId(id)
                .orElseThrow(() -> new NoSuchElementException("Collection not found with ID: " + id));
        return ResponseEntity.ok(mapRevisionsToDtoList(collectionRepository.findRevisions(collection.getCollectionNo())));
    }

    @GetMapping("/salesTarget/{id}")  public ResponseEntity<List<AuditHistoryDto>> getSalesTargetHistory(@PathVariable Long id) { return ResponseEntity.ok(mapRevisionsToDtoList(salesTargetRepository.findRevisions(id))); }
}
