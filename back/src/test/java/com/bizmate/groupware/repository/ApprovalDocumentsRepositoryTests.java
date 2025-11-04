//package com.bizmate.groupware.repository;
//
//import com.bizmate.groupware.domain.ApprovalDocuments;
//import com.bizmate.groupware.domain.DocumentStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//
//@SpringBootTest
//public class ApprovalDocumentsRepositoryTests {
//
//    @Autowired
//    private ApprovalDocumentsRepository approvalDocumentsRepository;
//
//    @Test
//    public void createNewDocumentTest() {
//        ApprovalDocuments doc = new ApprovalDocuments();
//        long authorId = 120251001001L;
//        long authorRoleId = 10L;
//
//        doc.setDocId("TEST-DOC-20250101-002");
//        doc.setTitle("왕찬웅 퇴직서");
//        doc.setDocType("퇴직서");
//
//        doc.setStatus(DocumentStatus.PENDING);
//
//
//        doc.setDocContentJson("[{title : 개인적인 사정, text : 나 돌아갈래}]");
//        doc.setApprovalLineJson("[{\"approverId\":\"안재성\", \"order\":1},{\"approverId\":\"한유주\", \"order\":2}]");
//
//        doc.setAuthorUserId(authorId);
//        doc.setAuthorEmpId(220250428L);
//        doc.setAuthorRoleId(authorRoleId);
//
//        // 💡 수정: 마지막 남은 필드인 currentApproverIndex를 명시적으로 0으로 설정
//        doc.setCurrentApproverIndex(0);
//
//
//        doc.setFinalDocNumber(null);
//
//        ApprovalDocuments savedDocument = approvalDocumentsRepository.save(doc);
//
//        System.out.println("Saved Document ID: " + savedDocument.getDocId());
//
//
//    }
//}
