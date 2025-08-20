package org.example.dto;

import java.time.LocalDateTime;

public class TaskCreateDTO {
    private String title;             // 必填
    private String description;       // 选填
    private Integer priority;         // 1=低,2=中,3=高  (默认2)
    private LocalDateTime deadline;   // 截止时间，可空
    private Integer status;           // 0=待办,1=进行中,2=已完成 (默认0)

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
