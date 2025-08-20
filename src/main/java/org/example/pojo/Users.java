package org.example.pojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Users {
    /** 用户唯一标识（主键，自增） */
    private Long id;

    /** 用户名（唯一） */
    private String username;

    /** 用户密码（BCrypt 加密后存储） */
    private String password;

    /** 用户邮箱（唯一） */
    private String email;

    /** 用户手机号 */
    private String phone;

    /** 用户角色，例如 ROLE_USER、ROLE_ADMIN 等 */
    private String role;

    /** 用户创建时间（默认当前时间戳） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 用户信息最后更新时间（默认当前时间戳） */
    private LocalDateTime updatedAt;

    /** 是否逻辑删除（0-未删除，1-已删除） */
    private Integer isDeleted;

    /** 登录失败次数 */
    private Integer loginFailCount;

    /** 账户锁定时间 */
    private LocalDateTime lockTime;

    /** 账户状态（0-正常，1-锁定） */
    private Integer status;

    private String avatar; // 用户头像 URL

}