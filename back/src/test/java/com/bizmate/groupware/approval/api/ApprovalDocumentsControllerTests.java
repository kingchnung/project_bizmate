package com.bizmate.groupware.approval.api;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class ApprovalDocumentsControllerTests {

//    @Autowired
//    MockMvc mockMvc;
//    @Autowired
//    ObjectMapper om;
//
//    @Test
//    @DisplayName("상신 후 단건 조회/검색/승인까지 OK")
//    void submitGetSearchApproveFlow() throws Exception {
//        // ---------- 1) payload 준비 ----------
//        var docPayload = Map.of(
//                "title", "인사발령 보고서",
//                "docType", DocumentType.REPORT.name(),  // enum 문자열
//                "departmentCode", "HR",
//                "userId", 1001,
//                "roleId", 10,
//                "empId", 220250428,
//                "docContent", Map.of("title","인사발령","text","2025-10-05자 인사발령"),
//                "approvalLine", List.of(
//                        // approverId는 승인자 userId와 문자열 동일하게 맞춤(권한검증 단순 비교)
//                        Map.of("order",1, "approverId","1001", "decision", Decision.PENDING.name()),
//                        Map.of("order",2, "approverId","1002", "decision", Decision.PENDING.name())
//                )
//        );
//        String submitJson = om.writeValueAsString(docPayload);
//
//        // ---------- 2) submit ----------
//        var submitResult = mockMvc.perform(post("/api/v1/approvals/submit")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(submitJson))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id", startsWith("HR-")))
//                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
//                .andReturn();
//
//        String docId = om.readTree(submitResult.getResponse().getContentAsString())
//                .get("id").asText();
//
//        // ---------- 3) get by id ----------
//        mockMvc.perform(get("/api/v1/approvals/{docId}", docId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title", is("인사발령 보고서")))
//                .andExpect(jsonPath("$.approvalLine", hasSize(2)));
//
//        // ---------- 4) search ----------
//        mockMvc.perform(get("/api/v1/approvals")
//                        .param("status","IN_PROGRESS")
//                        .param("keyword","보고서")
//                        .param("page","0").param("size","10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", not(empty())))
//                .andExpect(jsonPath("$.content[0].id", notNullValue()));
//
//        // ---------- 5) approve step #1 ----------
//        var approveBody = Map.of("actorUserId", 1001, "comment", "OK");
//        mockMvc.perform(post("/api/v1/approvals/{docId}/approve", docId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(approveBody)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status", is("IN_PROGRESS"))); // 다음 결재자로 이동
//
//        // ---------- 6) approve step #2 (최종 승인) ----------
//        var approve2Body = Map.of("actorUserId", 1002, "comment", "OK-2");
//        mockMvc.perform(post("/api/v1/approvals/{docId}/approve", docId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(approve2Body)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status", is("APPROVED")))
//                .andExpect(jsonPath("$.finalDocNumber", startsWith("DOC-")));
//
//    }
}
