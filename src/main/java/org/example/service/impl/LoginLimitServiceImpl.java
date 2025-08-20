package org.example.service.impl;

import org.example.service.LoginLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LoginLimitServiceImpl implements LoginLimitService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int MAX_FAILS = 5;
    private static final int LOCK_MINUTES = 15;

    @Override
    public boolean isLocked(String ip) {
        String key = "login:fail:" + ip;
        String count = redisTemplate.opsForValue().get(key);
        return count != null && Integer.parseInt(count) >= MAX_FAILS;
    }

    @Override
    public void recordFail(String ip) {
        String key = "login:fail:" + ip;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(LOCK_MINUTES));
    }

    @Override
    public void reset(String ip) {
        redisTemplate.delete("login:fail:" + ip);
    }
}
