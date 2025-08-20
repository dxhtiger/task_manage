package org.example.service.impl;

import org.example.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("oss")
public class OssStorageServiceImpl implements FileStorageService {

    @Override
    public String upload(MultipartFile file, Long userId) throws Exception {
        // 你可以在这里集成阿里云 OSS SDK 的上传逻辑
        // 成功后返回完整的外链地址
        return "https://your-oss-url/avatar_" + userId + ".jpg";
    }
}
