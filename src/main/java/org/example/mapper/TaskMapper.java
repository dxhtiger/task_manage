package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.dto.TaskQueryDTO;
import org.example.pojo.Tasks;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TaskMapper {

    int insert(Tasks t);

    int update(Tasks t);

    Tasks selectById(@Param("id") Long id, @Param("userId") Long userId);

    int softDelete(@Param("id") Long id, @Param("userId") Long userId);

    List<Tasks> pageQuery(@Param("q") TaskQueryDTO q, @Param("userId") Long userId);
    // 新增：根据ID更新状态
    int updateStatus(@Param("userId") Long userId,
                     @Param("id") Long id,
                     @Param("status") Integer status);

    Tasks selectByIdAdmin(Long id);
    int updateByIdAdmin(Tasks t);
    int softDeleteAdmin(Long id);
    List<Tasks> pageQueryAll(@Param("q") TaskQueryDTO q);
    int updateStatusAdmin(@Param("id") Long id, @Param("status") Integer status);
    /** 查询“当天截止且未完成”的任务，并带出用户邮箱 */
    List<DueTaskRow> findDueTasks(@Param("date") LocalDate date,
                                  @Param("doneStatus") Integer doneStatus);

    /** 轻量 VO（只带发信需要的字段） */
    class DueTaskRow {
        private Long userId;
        private String email;
        private Long taskId;
        private String title;
        private java.time.LocalDateTime deadline;
        // getter/setter
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public java.time.LocalDateTime getDeadline() { return deadline; }
        public void setDeadline(java.time.LocalDateTime deadline) { this.deadline = deadline; }
    }
}
