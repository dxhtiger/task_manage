// src/main/java/org/example/controller/TestMailController.java
package org.example.controller;

import org.example.service.mail.MailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/_test/mail")   // 下划线前缀，方便后面统一下线
public class TestMailController {

    private final MailService mailService;
    public TestMailController(MailService mailService) { this.mailService = mailService; }

    @PostMapping("/send")
    public String send(@RequestParam String to,
                       @RequestParam String subject,
                       @RequestParam String text) {
        mailService.sendText(to, subject, text);
        return "ok";
    }
}
