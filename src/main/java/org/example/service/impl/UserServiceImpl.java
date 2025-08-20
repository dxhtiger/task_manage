package org.example.service.impl;
import java.util.List;
import java.util.Arrays;

import org.example.common.ApiResponse;
import org.example.mapper.UserMapper;
import org.example.pojo.Users;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service // 表示这是一个 Service 组件
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    /**
     * 用户注册
     */
    @Override
    public boolean register(Users user) {
        // 根据用户名查询，检查是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            return false; // 用户已存在
        }
        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        // 插入数据库
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 用户登录，返回 JWT Token
     */
    @Override
    public String login(String username, String password) {
        Users dbUser = userMapper.selectByUsername(username);
        if (dbUser != null && passwordEncoder.matches(password, dbUser.getPassword())) {
            // 登录成功，生成 Token
            return JwtUtil.generateToken(dbUser);
        }
        return null; // 登录失败
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public Users findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 根据邮箱查询用户
     */
    @Override
    public Users findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    /**
     * 修改密码
     */
    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        Users dbUser = userMapper.selectById(userId);
        if (dbUser != null && passwordEncoder.matches(oldPassword, dbUser.getPassword())) {
            // 判断新旧密码是否相同（注意此处用明文比较）
            if (oldPassword.equals(newPassword)) {
                return false; // 新旧密码不能相同
            }
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            dbUser.setPassword(encodedNewPassword);
            return userMapper.updateUser(dbUser) > 0;
        }
        return false;
    }

    /**
     * 获取个人信息
     */
    @Override
    public Users getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 更新用户信息（比如修改邮箱、手机号）
     */
    @Override
    public boolean updateUserInfo(Users user) {
        return userMapper.updateUser(user) > 0;
    }
    @Override
    public void increaseLoginFailCount(String username) {
        // TODO: 写入更新登录失败次数的真实逻辑
        System.out.println("增加用户 " + username + " 的登录失败次数");
    }

    @Override
    public void resetLoginFailCount(String username) {
        // TODO: 写入重置登录失败次数的真实逻辑
        System.out.println("重置用户 " + username + " 的登录失败次数");
    }

    @Override
    public boolean isAccountLocked(String username) {
        return false;
    }
    // 如果需要，还可以添加锁定账户等方法
    /**
     * 上传用户头像
     */
    @Override
    public ApiResponse<String> uploadAvatar(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.fail("上传文件为空");
        }

        try {
            // 1. 获取文件原始名和后缀
            String originalFilename = file.getOriginalFilename();
            String fileExt = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                    : "";

            // 2. 校验扩展名
            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png");
            if (!allowedExtensions.contains(fileExt)) {
                return ApiResponse.fail("不支持的文件格式，仅允许上传 JPG、JPEG、PNG 图片");
            }

            // 3. 校验 MIME 类型
            String contentType = file.getContentType();
            List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png");
            if (contentType == null || !allowedTypes.contains(contentType)) {
                return ApiResponse.fail("文件类型错误，仅支持 JPG 和 PNG");
            }

            // 4.  使用项目根路径 + 相对目录
            String fileName = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExt;
            String uploadDir = System.getProperty("user.dir") + "/uploads/avatar/";

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fullPath = uploadDir + fileName;
            file.transferTo(new File(fullPath));

            // 5. 构造访问 URL（静态资源访问前缀）
            String avatarUrl = "/static/avatar/" + fileName;

            // 6. 更新数据库
            Users user = new Users();
            user.setId(userId);
            user.setAvatar(avatarUrl);

            boolean updated = userMapper.updateUser(user) > 0;
            return updated ? ApiResponse.success("上传成功", avatarUrl)
                    : ApiResponse.fail("数据库更新失败");

        } catch (IOException e) {
            return ApiResponse.fail("上传异常：" + e.getMessage());
        }
    }


}

