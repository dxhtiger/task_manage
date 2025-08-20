// org.example.schedule.DueTaskScheduler
package org.example.schedule;

import org.example.mapper.TaskMapper;
import org.example.service.mail.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DueTaskScheduler {

    private final TaskMapper taskMapper;
    private final MailService mailService;

    // 从配置读取“已完成”的状态值，默认 2
    @Value("${tasks.status.done:2}")
    private Integer doneStatus;

    public DueTaskScheduler(TaskMapper taskMapper, MailService mailService) {
        this.taskMapper = taskMapper;
        this.mailService = mailService;
    }

    /** 每天 8:00 触发（服务器时区） */
    @Scheduled(cron = "0 0 8 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")  // 每分钟
    public void pushTodayDueTasks() {
        LocalDate today = LocalDate.now();

        var rows = taskMapper.findDueTasks(today, doneStatus);
        if (rows == null || rows.isEmpty()) return;
        System.out.println("今日到期任务数: " + rows.size());

        // 按邮箱分组 -> 每个用户一封邮件
        Map<String, List<TaskMapper.DueTaskRow>> grouped =
                rows.stream().filter(r -> r.getEmail() != null && !r.getEmail().isBlank())
                        .collect(Collectors.groupingBy(TaskMapper.DueTaskRow::getEmail));

        grouped.forEach((email, list) -> {
            List<String> lines = new ArrayList<>();
            lines.add("您好，以下是您今天到期的任务：");
            lines.add("");
            for (var r : list) {
                String deadlineStr = (r.getDeadline() == null) ? "-" : r.getDeadline().toString();
                lines.add(String.format("· #%d %s （截止：%s）", r.getTaskId(), r.getTitle(), deadlineStr));
            }
            lines.add("");
            lines.add("— 来自任务管理系统");

            mailService.sendDueTasksEmail(email, lines);
        });
    }
}
