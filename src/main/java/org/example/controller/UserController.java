package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.common.ApiResponse;
import org.example.pojo.Users;
import org.example.service.LoginLimitService;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginLimitService loginLimitService;



    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody Users user) {
        // 1. 基本字段非空校验
        if (user.getUsername() == null || user.getPassword() == null ||
                user.getEmail() == null || user.getPhone() == null) {
            return ApiResponse.fail("用户名、密码、邮箱、手机号不能为空");
        }

        // 2. 密码强度校验：包含大小写字母、数字，长度至少8位
        String password = user.getPassword();
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!password.matches(passwordRegex)) {
            return ApiResponse.fail("密码必须包含大写字母、小写字母、数字，且不少于8位");
        }

        // 3. 邮箱格式校验
        String email = user.getEmail();
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.fail("邮箱格式不正确");
        }

        // 4. 调用注册逻辑
        boolean success = userService.register(user);
        return success ? ApiResponse.success("注册成功", null) : ApiResponse.fail("用户已存在");
    }


    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestParam String username, @RequestParam String password,
                                     HttpServletRequest request) {

        // 限制登录失败次数（详见下一步）
        String ip = request.getRemoteAddr();

        // 检查用户是否被锁定
        if (loginLimitService.isLocked(ip)) {
            return ApiResponse.fail("登录失败次数过多，请15分钟后再试");
        }

        String token = userService.login(username, password);
        if (token != null) {
            return ApiResponse.success("登录成功", token);
        } else {
            // 登录失败 + 记录失败次数（详见下一步）
            loginLimitService.recordFail(ip);
            return ApiResponse.fail("用户名或密码错误");
        }
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/{username}")
    public ApiResponse<Users> getUserByUsername(@PathVariable String username) {
        Users user = userService.findByUsername(username);
        return user != null ? ApiResponse.success(user) : ApiResponse.fail("用户不存在");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestParam String oldPassword,
                                              @RequestParam String newPassword,
                                              HttpServletRequest request) {

        // 从 Header 中提取 Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.fail("缺少或无效的 Token");
        }

        token = token.substring(7); // 去掉 Bearer 前缀

        // 校验 Token
        if (!JwtUtil.validate(token)) {
            return ApiResponse.fail("无效的 Token");
        }

        Long userId = JwtUtil.getUserIdFromToken(token);
        Users user = userService.getUserInfo(userId);

        if (user == null) {
            return ApiResponse.fail("用户不存在");
        }

        if (oldPassword.equals(newPassword)) {
            return ApiResponse.fail("新密码不能和旧密码相同");
        }

        boolean success = userService.updatePassword(userId, oldPassword, newPassword);
        return success ? ApiResponse.success("修改成功", "") : ApiResponse.fail("原密码错误");
    }


    /**
     * 获取用户信息（根据 userId）
     */
    @GetMapping("/info/{userId}")
    public ApiResponse<Users> getUserInfo(@PathVariable Long userId) {
        Users user = userService.getUserInfo(userId);
        return user != null ? ApiResponse.success(user) : ApiResponse.fail("用户不存在");
    }

    /**
     * 更新用户信息（邮箱、手机号等）
     */
    @PostMapping("/update")
    public ApiResponse<String> updateUser(@RequestBody Users user) {
        boolean success = userService.updateUserInfo(user);
        return success ? ApiResponse.success("更新成功", null) : ApiResponse.fail("更新失败");
    }

    /**
     * 查询用户是否被锁定
     */
    @GetMapping("/{username}/locked")
    public ApiResponse<Boolean> isAccountLocked(@PathVariable String username) {
        boolean locked = userService.isAccountLocked(username);
        return ApiResponse.success(locked);
    }

    /**
     *头像上传
     */
    @PostMapping("/upload-avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.fail("缺少或无效的 Token");
        }
        token = token.substring(7);

        if (!JwtUtil.validate(token)) {
            return ApiResponse.fail("无效的 Token");
        }

        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ApiResponse.fail("Token 无法识别用户");
        }

        // 只调用 service
        return userService.uploadAvatar(file, userId);
    }


}
