// org.example.service.mail.MailService
package org.example.service.mail;

import org.example.pojo.Tasks;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;


public interface MailService {


    void sendDueTasksEmail(String to, List<String> lines) ;

    void sendText(String to, String subject, String content);


    void sendDueTaskDigest(String to, List<Tasks> tasks);
}
