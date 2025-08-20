// org.example.log.OpLogAspect
package org.example.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.pojo.OperationLog;
import org.example.service.log.OpLogService;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Order(1)
public class OpLogAspect {

    private final OpLogService logService;
    private final HttpServletRequest request;
    private final ObjectMapper om = new ObjectMapper();

    public OpLogAspect(OpLogService logService, HttpServletRequest request) {
        this.logService = logService;
        this.request = request;
    }

    @Around("@annotation(op)")
    public Object around(ProceedingJoinPoint pjp, Op op) throws Throwable {
        OperationLog log = new OperationLog();
        log.setAction(op.value());
        log.setMethod(pjp.getSignature().toShortString());
        log.setUri(request.getRequestURI());
        log.setIp(request.getRemoteAddr());
        log.setCreatedAt(LocalDateTime.now());

        // 从 Spring Security 里取当前用户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.setUsername(auth.getName());
            // 你也可以把 JwtFilter 里放到 request 的 userId 取出来
            Object uid = request.getAttribute("currentUserId");
            if (uid instanceof Long u) log.setUserId(u);
        }

        if (op.saveArgs()) {
            try { log.setParams(om.writeValueAsString(pjp.getArgs())); } catch (Exception ignored) {}
        }

        try {
            Object ret = pjp.proceed();
            log.setSuccess(true);
            try { log.setResult(om.writeValueAsString(ret)); } catch (Exception ignored) {}
            return ret;
        } catch (Throwable e) {
            log.setSuccess(false);
            log.setError(e.getMessage());
            throw e;
        } finally {
            logService.save(log);
        }
    }
}
