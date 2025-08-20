package org.example.service;

public interface LoginLimitService {
    boolean isLocked(String ip);
    void recordFail(String ip);
    void reset(String ip);
}
