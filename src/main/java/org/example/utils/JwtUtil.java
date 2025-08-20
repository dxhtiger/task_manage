package org.example.utils;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // 签名密钥（建议放到配置文件中）
    private static final String SECRET_KEY = "mySecretKey";

    // token 有效时间（单位：毫秒）示例：2小时
    private static final long EXPIRATION = 2 * 60 * 60 * 1000;

    /**
     * 生成 JWT Token
     */
    public static String generateToken(org.example.pojo.Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("roles", user.getRole()); // 保存角色

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 从 token 中获取用户名
     */
    public static String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 从 token 中获取用户 ID
     */
    public static Long getUserIdFromToken(String token) {
        Object userId = getClaims(token).get("userId");
        return userId != null ? Long.parseLong(userId.toString()) : null;
    }

    /**
     * 验证 token 是否过期
     */
    public static boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 校验 token（示例：校验签名 + 是否过期）
     */
    public static boolean validate(String token) {
        try {
            getClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 解析 token 获得 claims
     */
    private static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getRoles(String token) {
        Object roles = getClaims(token).get("roles");
        return roles == null ? null : roles.toString();
    }
}
