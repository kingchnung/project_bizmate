//package com.bizmate.salesPage.client.repository;
//
//import com.bizmate.salesPages.client.domain.Client;
//import com.bizmate.salesPages.client.repository.ClientRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//@SpringBootTest
//@Slf4j
//public class ClientRepositoryTests {
//    @Autowired
//    private ClientRepository clientRepository;
//
//    @Test
//    public void testInsert(){
//        for(int i = 11; i <= 30; i++){
//            Client client = Client.builder()
//                    .clientId("132-86-18174-" + i)
//                    .clientCompany("테스트용" + i)
//                    .registrationDate(LocalDate.now())
//                    .userId("user00" + i)
//                    .build();
//            clientRepository.save(client);
//        }
//    }
//
//    @Test
//    public void testRead() {
//        Long clientNo = 10L;
//
//        Optional<Client> result = clientRepository.findById(clientNo);
//        Client client = result.orElseThrow();
//        log.info("데이터조회(10L): {}",client);
//    }
//
//    @Test
//    public void testModify() {
//        Long clientNo = 21L;
//
//        Optional<Client> result = clientRepository.findById(clientNo);
//
//        Client client = result.orElseThrow();
//        client.changeClientId("111-22-33333");
//        client.changeClientCeo("한유주");
//        client.changeClientCompany("유주짱");
//
//        Client clientResult = clientRepository.save(client);
//        log.info("수정된 데이터: {}", clientResult);
//    }
//
//    @Test
//    public void testDelete() {
//        Long clientNo = 22L;
//
//        clientRepository.deleteById(clientNo);
//    }
//
//    @Test
//    public void testPaging() {
//        Pageable pageable = PageRequest.of(0,10,Sort.by("clientNo").descending());
//        Page<Client> result = clientRepository.findAll(pageable);
//        log.info("총 데이터 수: {}", result.getTotalElements());
//        result.getContent().stream().forEach(client -> log.info(client.toString()));
//    }
//
//    @Test
//    public void findByClientNoTest() {
//        Client clientNoSearch = clientRepository.findByClientNo(21L);
//        log.info(clientNoSearch.toString());
//    }
//}
