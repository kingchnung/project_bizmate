package com.bizmate.groupware.approval.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Async
    public void sendApprovalRequestMail(String to, String approverName, String docTitle, String docId, String requesterName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[전자결재 알림] 결재 요청: " + docTitle);
        msg.setText(
                approverName + "님,\n\n" +
                        requesterName + "님이 결재 요청하신 문서 [" + docTitle + "]가 대기 중입니다.\n" +
                        "👉 결재 바로가기: http://localhost:3000/approval/detail/" + docId + "\n\n" +
                        "BizMate 전자결재 시스템"
        );
        mailSender.send(msg);
        log.info("📨 결재 요청 메일 전송 완료 → {}", to);
    }

    @Async
    public void sendApprovalCompleteMail(String to, String docTitle, String docId, String lastApproverName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[전자결재 알림] 문서 최종 승인 완료: " + docTitle);
        msg.setText(
                "문서 [" + docTitle + "]이(가) 최종 승인되었습니다.\n" +
                        "최종 승인자: " + lastApproverName + "\n\n" +
                        "문서 보기: http://localhost:3000/approval/detail/" + docId + "\n\n" +
                        "BizMate 전자결재 시스템"
        );
        mailSender.send(msg);
        log.info("📨 승인 완료 메일 전송 완료 → {}", to);
    }

    @Async
    public void sendRejectMail(String to, String docTitle, String docId, String rejecterName, String reason) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[전자결재 알림] 문서 반려: " + docTitle);
        msg.setText(
                "문서 [" + docTitle + "]이(가) 반려되었습니다.\n" +
                        "반려자: " + rejecterName + "\n" +
                        "사유: " + reason + "\n\n" +
                        "문서 확인: http://localhost:3000/approval/detail/" + docId + "\n\n" +
                        "BizMate 전자결재 시스템"
        );
        mailSender.send(msg);
        log.info("📨 반려 메일 전송 완료 → {}", to);
    }
}