package org.example.service.impl;
import org.example.common.ApiResponse;
import org.example.mapper.UserMapper;
import org.example.pojo.Users;
import org.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service("local")
public class LocalStorageServiceImpl implements FileStorageService {
    @Value("${file.local-path}")
    private String uploadDir;

    @Value("${file.url-prefix}")
    private String urlPrefix;

    private final UserMapper userMapper;

    public LocalStorageServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String upload(MultipartFile file, Long userId) throws Exception {
        if (file.isEmpty()) throw new Exception("文件为空");

        String originalFilename = file.getOriginalFilename();
        String fileExt = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.')) : ".jpg";

        String filename = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExt;

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        File saveFile = new File(uploadDir + filename);
        file.transferTo(saveFile);

        String avatarUrl = urlPrefix + filename;

        // 更新数据库
        Users user = new Users();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        userMapper.updateUser(user);

        return avatarUrl;
    }
}
