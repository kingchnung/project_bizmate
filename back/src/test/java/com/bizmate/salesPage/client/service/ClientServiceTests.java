//package com.bizmate.salesPage.client.service;
//
//
//import com.bizmate.common.dto.PageRequestDTO;
//import com.bizmate.common.dto.PageResponseDTO;
//import com.bizmate.salesPages.client.dto.ClientDTO;
//import com.bizmate.salesPages.client.service.ClientService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//
//@SpringBootTest
//@Slf4j
//public class ClientServiceTests {
//    @Autowired
//    private ClientService clientService;
//
//    @Test
//    public void testRegister() {
//        ClientDTO clientDTO = ClientDTO.builder()
//                .clientId("123-15-465463")
//                .clientCompany("테스트용3")
//                .clientCeo("test3")
//                .clientBusinessType("제조업")
//                .clientAddress("경기도 남양주시")
//                .clientContact("031-547-4653")
//                .clientNote("특이사항 없음")
//                .empName("한유주")
//                .clientEmail("test3@naver.com")
//                .userId("test003")
//                .build();
//
//        Long clientNo = clientService.clientRegister(clientDTO);
//        log.info("ClientNO: {}", clientNo);
//    }
//
//    @Test
//    public void testGet() {
//        Long clientNo = 33L;
//        ClientDTO clientDTO = clientService.clientGet(clientNo);
//        log.info("데이터: {}", clientDTO);
//    }
//
//    @Test
//    public void testList() {
//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(2).size(10).build();
//
//        PageResponseDTO<ClientDTO> response = clientService.clientList(pageRequestDTO);
//        log.info("PageResponseDTO: {}", response);
//    }
//}
