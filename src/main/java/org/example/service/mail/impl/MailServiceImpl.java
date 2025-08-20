package org.example.service.mail.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.pojo.Tasks;
import org.example.service.mail.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendDueTasksEmail(String to, List<String> lines) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from); // 必须和 spring.mail.username 一致
            helper.setTo(to);
            helper.setSubject("今日到期任务提醒");

            // 拼接正文
            String body = String.join("<br/>", lines);
            helper.setText(body, true); // 第二个参数 true 表示 HTML 格式

            mailSender.send(message);

            System.out.println("📧 邮件已发送到 " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("发送任务到期提醒邮件失败", e);
        }
    }

    @Override
    public void sendText(String to, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        mailSender.send(msg);
    }

    @Override
    public void sendDueTaskDigest(String to, List<Tasks> tasks) {
        StringBuilder body = new StringBuilder("今天到期的任务：\n\n");
        tasks.forEach(t -> body.append("• ")
                .append(t.getTitle())
                .append("（截止：").append(t.getDeadline()).append("）\n"));
        sendText(to, "任务提醒（今日到期）", body.toString());
    }
}
