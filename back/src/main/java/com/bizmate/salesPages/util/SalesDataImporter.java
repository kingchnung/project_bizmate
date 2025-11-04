package com.bizmate.salesPages.util;

import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.repository.ClientRepository;
import com.bizmate.salesPages.management.collections.domain.Collection;
import com.bizmate.salesPages.management.collections.repository.CollectionRepository;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import com.bizmate.salesPages.report.salesTarget.repository.SalesTargetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.bizmate.salesPages.util.Csv.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesDataImporter implements CommandLineRunner {

    private final ResourceLoader resourceLoader;

    private final ClientRepository clientRepository;
    private final SalesTargetRepository salesTargetRepository;
    private final OrderRepository orderRepository;
    private final SalesRepository salesRepository;
    private final CollectionRepository collectionRepository;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    // =========================
    // Properties 주입
    // =========================
    @Value("${bizmate.data.import.enabled:true}")
    private boolean enabled;

    @Value("${bizmate.data.import.skip-if-exists:true}")
    private boolean skipIfExists;

    @Value("${bizmate.data.import.clients:classpath:data/clients.csv}")
    private String clientsCsv;

    @Value("${bizmate.data.import.sales-targets:classpath:data/sales_targets.csv}")
    private String targetsCsv;

    @Value("${bizmate.data.import.orders:classpath:data/orders.csv}")
    private String ordersCsv;

    @Value("${bizmate.data.import.order-items:classpath:data/order_items.csv}")
    private String orderItemsCsv;

    @Value("${bizmate.data.import.sales:classpath:data/sales.csv}")
    private String salesCsv;

    @Value("${bizmate.data.import.sales-items:classpath:data/sales_items.csv}")
    private String salesItemsCsv;

    @Value("${bizmate.data.import.collections:classpath:data/collections.csv}")
    private String collectionsCsv;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("▶▶▶ 실제 데이터 임포트 시작 | profile='{}' | enabled={} | skipIfExists={}",
                activeProfile, enabled, skipIfExists);

        if (!enabled) {
            log.info("⏸ 데이터 임포트 비활성화됨 (bizmate.data.import.enabled=false)");
            return;
        }

        // 리소스 가용성 미리 점검
        debugResource("clients", clientsCsv);
        debugResource("sales-targets", targetsCsv);
        debugResource("orders", ordersCsv);
        debugResource("order-items", orderItemsCsv);
        debugResource("sales", salesCsv);
        debugResource("sales-items", salesItemsCsv);
        debugResource("collections", collectionsCsv);

        if (!(skipIfExists && clientRepository.count() > 0)) importClients(); else log.info("↷ clients: skip (count={})", clientRepository.count());
        if (!(skipIfExists && salesTargetRepository.count() > 0)) importSalesTargets(); else log.info("↷ targets: skip (count={})", salesTargetRepository.count());
        if (!(skipIfExists && orderRepository.count() > 0)) importOrdersAndItems(); else log.info("↷ orders: skip (count={})", orderRepository.count());
        if (!(skipIfExists && salesRepository.count() > 0)) importSalesAndItems(); else log.info("↷ sales: skip (count={})", salesRepository.count());
        if (!(skipIfExists && collectionRepository.count() > 0)) importCollections(); else log.info("↷ collections: skip (count={})", collectionRepository.count());

        log.info("✅ 실제 데이터 임포트 완료");
    }

    private void debugResource(String name, String location) {
        try {
            var r = resourceLoader.getResource(location);
            log.info("• 리소스 [{}]: {} | exists={} | readable={}",
                    name, location, r.exists(), r.isReadable());
        } catch (Exception e) {
            log.warn("• 리소스 [{}]: {} | 확인 중 예외: {}", name, location, e.getMessage());
        }
    }

    private void importClients() {
        var rows = read(resourceLoader, clientsCsv);
        log.info("clients.csv rows={}", rows.size());
        if (rows.isEmpty()) { log.warn("⚠️ clients.csv 없음 또는 비어 있음"); return; }

        List<Client> list = new ArrayList<>();
        for (var r : rows) {
            Client c = Client.builder()
                    .clientId(s(r,"client_id"))
                    .clientCompany(s(r,"client_company"))
                    .clientCeo(s(r,"client_ceo"))
                    .clientBusinessType(s(r,"client_business_type"))
                    .clientAddress(s(r,"client_address"))
                    .clientContact(s(r,"client_contact"))
                    .clientEmail(s(r,"client_email"))
                    .writer(Optional.ofNullable(s(r,"writer")).orElse("SYSTEM"))
                    .userId(Optional.ofNullable(s(r,"user_id")).orElse("admin"))
                    .validationStatus(Optional.ofNullable(b(r,"validation_status")).orElse(Boolean.TRUE))
                    .build();
            list.add(c);
        }
        clientRepository.saveAll(list);
        log.info("✅ 거래처 데이터 저장 OK | inputRows={} | saved={}", rows.size(), list.size());
    }

    private void importSalesTargets() {
        var rows = read(resourceLoader, targetsCsv);
        log.info("sales_targets.csv rows={}", rows.size());
        if (rows.isEmpty()) { log.warn("⚠️ sales_targets.csv 없음 또는 비어 있음"); return; }

        List<SalesTarget> list = new ArrayList<>();
        for (var r : rows) {
            SalesTarget st = SalesTarget.builder()
                    .targetYear(Integer.valueOf(s(r,"target_year")))
                    .targetMonth(Integer.valueOf(s(r,"target_month")))
                    .targetAmount(bd(r,"target_amount"))
                    .writer(Optional.ofNullable(s(r,"writer")).orElse("SYSTEM"))
                    .userId(Optional.ofNullable(s(r,"user_id")).orElse("admin"))
                    .build();
            list.add(st);
        }
        salesTargetRepository.saveAll(list);
        log.info("✅ 매출목표 데이터 저장 OK | inputRows={} | saved={}", rows.size(), list.size());
    }

    private void importOrdersAndItems() {
        var orders = read(resourceLoader, ordersCsv);
        var items  = read(resourceLoader, orderItemsCsv);
        log.info("orders.csv rows={}", orders.size());
        if (orders.isEmpty()) { log.warn("⚠️ orders.csv 없음 또는 비어 있음"); return; }

        // ✨ [추가] Client 데이터를 미리 맵으로 만듭니다.
        Map<String, Client> clientMap = clientRepository.findAll().stream()
                .collect(Collectors.toMap(Client::getClientId, c -> c));

        // itemMap: order_id 기준 묶기 (null 키는 제외)
        Map<String, List<Map<String,String>>> itemMap = items.stream()
                .filter(m -> s(m,"order_id") != null)
                .collect(Collectors.groupingBy(m -> s(m,"order_id")));

        List<Order> toSave = new ArrayList<>();
        for (var r : orders) {
            Client client = clientMap.get(s(r, "client_id"));
            if (client == null) {
                log.warn("⚠️ orders.csv의 client_id '{}'에 해당하는 Client 없음. 이 Order는 건너뜁니다.", s(r, "client_id"));
                continue; // 또는 예외 처리
            }

            Order o = Order.builder()
                    .orderId(s(r,"order_id"))
                    .orderIdDate(d(r,"order_id_date"))
                    .orderDate(d(r,"order_date"))
                    .orderDueDate(d(r,"order_due_date"))
                    .projectId(s(r,"project_id"))
                    .projectName(s(r,"project_name"))
                    .client(client)
                    .orderStatus(Optional.ofNullable(s(r,"order_status")).orElse("시작전"))
                    .orderNote(s(r,"order_note"))
                    .writer(Optional.ofNullable(s(r,"writer")).orElse("SYSTEM"))
                    .userId(Optional.ofNullable(s(r,"user_id")).orElse("admin"))
                    .build();

            List<OrderItem> oiList = new ArrayList<>();
            for (var ir : itemMap.getOrDefault(o.getOrderId(), List.of())) {
                OrderItem oi = OrderItem.builder()
                        .lineNum(Integer.valueOf(s(ir,"line_num")))
                        .itemName(s(ir,"item_name"))
                        .quantity(l(ir,"quantity"))
                        .unitPrice(bd(ir,"unit_price"))
                        .build();
                oi.calculateAmount();
                oi.setOrder(o);
                oi.setOrderNo( s(ir, "order_id") );
                oiList.add(oi);
            }

            o.updateOrderItems(oiList);
            o.calculateOrderAmount();
            toSave.add(o);
        }
        orderRepository.saveAll(toSave);
        log.info("✅ 주문 데이터 저장 OK | inputRows={} | saved={}", orders.size(), toSave.size());
    }

    private void importSalesAndItems() {
        var salesRows = read(resourceLoader, salesCsv);
        var salesItemRows  = read(resourceLoader, salesItemsCsv);
        log.info("sales.csv rows={}", salesRows.size());
        if (salesRows.isEmpty()) { log.warn("⚠️ sales.csv 없음 또는 비어 있음"); return; }

        // itemMap: sales_id 기준 묶기 (null 키 제외)
        Map<String, List<Map<String,String>>> itemMap = salesItemRows.stream()
                .filter(m -> s(m,"sales_id") != null)
                .collect(Collectors.groupingBy(m -> s(m,"sales_id")));

        List<Sales> toSave = new ArrayList<>();
        for (var r : salesRows) {
            String orderId = s(r,"order_id");
            Order order = (orderId == null) ? null :
                    // 🔧 보편적인 기본키 조회
                    orderRepository.findByOrderId(orderId).orElse(null);

            Sales sObj = Sales.builder()
                    .salesId(s(r,"sales_id"))
                    .salesIdDate(d(r,"sales_id_date"))
                    .salesDate(d(r,"sales_date"))
                    .deploymentDate(d(r,"deployment_date"))
                    .projectId(s(r,"project_id"))
                    .projectName(s(r,"project_name"))
                    .clientId(s(r,"client_id"))
                    .clientCompany(s(r,"client_company"))
                    .invoiceIssued(Optional.ofNullable(b(r,"invoice_issued")).orElse(Boolean.FALSE))
                    .order(order)
                    .writer(Optional.ofNullable(s(r,"writer")).orElse("SYSTEM"))
                    .userId(Optional.ofNullable(s(r,"user_id")).orElse("admin"))
                    .build();

            List<SalesItem> siList = new ArrayList<>();
            var rowsForThisSales = itemMap.get(sObj.getSalesId());

            if (rowsForThisSales == null || rowsForThisSales.isEmpty()) {
                if (order != null && order.getOrderItems() != null) {
                    for (var oi : order.getOrderItems()) {
                        SalesItem si = SalesItem.builder()
                                .lineNum(oi.getLineNum())
                                .itemName(oi.getItemName())
                                .quantity(oi.getQuantity())
                                .unitPrice(oi.getUnitPrice())
                                .unitVat(oi.getUnitVat())
                                .totalAmount(oi.getTotalAmount())
                                .build();
                        si.setSalesNo( sObj.getSalesId() ); // 부모 Sales의 ID를 사용 (혹은 oi.getOrderNo())
                        siList.add(si);
                    }
                }
            } else {
                for (var ir : rowsForThisSales) {
                    SalesItem si = SalesItem.builder()
                            .lineNum(Integer.valueOf(s(ir,"line_num")))
                            .itemName(s(ir,"item_name"))
                            .quantity(l(ir,"quantity"))
                            .unitPrice(bd(ir,"unit_price"))
                            .unitVat(bd(ir,"unit_vat"))
                            .totalAmount(bd(ir,"total_amount"))
                            .build();
                    si.setSalesNo( s(ir, "sales_id") );
                    siList.add(si);
                }
            }

            sObj.updateSalesItems(siList);
            sObj.calculateSalesAmount();
            toSave.add(sObj);

            if (order != null) {
                if (sObj.isInvoiceIssued() && sObj.getSalesAmount() != null
                        && sObj.getSalesAmount().compareTo(order.getOrderAmount()) == 0) {
                    order.changeOrderStatus("완료");
                } else if (sObj.isInvoiceIssued()) {
                    order.changeOrderStatus("진행중");
                }
            }
        }
        salesRepository.saveAll(toSave);
        log.info("✅ 판매 데이터 저장 OK | inputRows={} | saved={}", salesRows.size(), toSave.size());
    }

    private void importCollections() {
        var rows = read(resourceLoader, collectionsCsv);
        log.info("collections.csv rows={}", rows.size());
        if (rows.isEmpty()) { log.warn("⚠️ collections.csv 없음 또는 비어 있음"); return; }

        Map<String, Client> clientMap = clientRepository.findAll().stream()
                .collect(Collectors.toMap(Client::getClientId, c -> c));

        List<Collection> toSave = new ArrayList<>();
        for (var r : rows) {
            var client = clientMap.get(s(r,"client_id"));
            if (client == null) continue;

            Collection c = Collection.builder()
                    .collectionId(s(r,"collection_id"))
                    .collectionDate(d(r,"collection_date"))
                    .collectionMoney(bd(r,"collection_money"))
                    .collectionNote(s(r,"collection_note"))
                    .writer(Optional.ofNullable(s(r,"writer")).orElse("SYSTEM"))
                    .userId(Optional.ofNullable(s(r,"user_id")).orElse("admin"))
                    .client(client)
                    .build();
            toSave.add(c);
        }
        collectionRepository.saveAll(toSave);
        log.info("✅ 수금 데이터 저장 OK | inputRows={} | saved={}", rows.size(), toSave.size());
    }
}
