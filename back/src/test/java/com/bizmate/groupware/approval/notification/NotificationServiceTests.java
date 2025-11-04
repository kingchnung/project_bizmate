package com.bizmate.groupware.approval.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
public class NotificationServiceTests {
    @Autowired
    private JavaMailSender mailSender;

    @Test
    void sendTestMail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("cksdnd1232@gmail.com"); // ✅ spring.mail.username과 동일하게
        msg.setTo("cksdnd1232@gmail.com");
        msg.setSubject("고생이 많구나");
        msg.setText("열심히 하는 모습이 보기 좋아~");
        mailSender.send(msg);
    }
}
