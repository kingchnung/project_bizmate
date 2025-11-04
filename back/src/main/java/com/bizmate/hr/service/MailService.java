package com.bizmate.hr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendPasswordResetMail(String email, String tempPw) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[BizMate] 임시 비밀번호 안내");
        message.setText("""
                안녕하세요. BizMate 인사관리 시스템입니다.
                
                아래는 귀하의 임시 비밀번호입니다:
                ▶ %s
                
                로그인 후 반드시 새 비밀번호로 변경해주세요.
                
                감사합니다.
                """.formatted(tempPw));
        mailSender.send(message);
    }
}
