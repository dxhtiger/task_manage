package org.example.pojo;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class Tasks {
        private Long id;
        private Long userId;
        private String title;
        private String description;
        private Integer priority;     // 1低 2中 3高
        private LocalDateTime deadline;
        private Integer status;       // 0待办 1进行中 2已完成
        private Integer isDeleted;    // 0/1
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

}
