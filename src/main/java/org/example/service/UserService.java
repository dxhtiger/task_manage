package org.example.service;

import org.example.common.ApiResponse;
import org.example.pojo.Users;
import org.springframework.web.multipart.MultipartFile;


public interface  UserService {
    // 用户注册
    boolean register(Users user);

    // 用户登录（返回 JWT Token / 登录结果）
    String login(String username, String password);

    // 根据用户名查询用户（注册时做唯一性校验）
    Users findByUsername(String username);

    // 根据邮箱查询用户（注册时做唯一性校验）
    Users findByEmail(String email);

    // 修改密码
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    // 获取当前用户信息
    Users getUserInfo(Long userId);

    // 更新个人信息（邮箱、手机号等）
    boolean updateUserInfo(Users user);

    // 设置登录失败次数、锁定账户等（可选，做登录安全用）
    void increaseLoginFailCount(String username);
    void resetLoginFailCount(String username);
    boolean isAccountLocked(String username);
    // 头像上传
    ApiResponse<String> uploadAvatar(MultipartFile file, Long userId);
}
