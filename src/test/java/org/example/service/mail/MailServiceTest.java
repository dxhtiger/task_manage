// src/test/java/org/example/service/mail/MailServiceTest.java
package org.example.service.mail;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MailServiceTest {

    @Test
    void sendDueTasksEmail_shouldSend() {
        JavaMailSender sender = mock(JavaMailSender.class);
        MailService mailService = new MailService(sender);

        mailService.sendDueTasksEmail("to@example.com", List.of("A", "B"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender, times(1)).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertArrayEquals(new String[]{"to@example.com"}, msg.getTo());
        assertEquals("今日到期任务提醒", msg.getSubject());
        assertEquals("A\nB", msg.getText());
    }
}
