package org.example.dto;

import java.time.LocalDateTime;

public class TaskUpdateDTO {
    private Long id;                  // 必填
    private String title;             // 选填
    private String description;       // 选填
    private Integer priority;         // 选填
    private LocalDateTime deadline;   // 选填
    private Integer status;           // 选填

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
