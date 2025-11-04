package com.bizmate.groupware.approval.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ConsoleNotificationAdapter implements NotificationPort {
    @Override
    public void notifyUsers(List<String> emails, String subject, String body) {
        log.info("NOTIFY -> to={}, subject={}, body={}", emails, subject, body);
    }
}
