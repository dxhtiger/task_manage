package org.example.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 放行的接口（按需增减）
    private static final String[] WHITE_LIST = {
            "/users/login",
            "/users/register",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    // 这些路径不需要过滤
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String p : WHITE_LIST) {
            if (PATH_MATCHER.match(p, uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith(BEARER)) {
            String token = auth.substring(BEARER.length());
            if (JwtUtil.validate(token)) {
                String username = JwtUtil.getUsername(token);
                Long userId = JwtUtil.getUserIdFromToken(token);

                // 从 token 里拿角色（如 "ROLE_USER" 或 "ROLE_ADMIN,ROLE_USER"）
                String roles = JwtUtil.getRoles(token); // 自己在 JwtUtil 里实现从 claims 取 "roles"
                List<GrantedAuthority> authorities =
                        (roles == null || roles.isBlank())
                                ? Collections.emptyList()
                                : Arrays.stream(roles.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 方便业务层直接拿当前用户ID
                request.setAttribute("currentUserId", userId);
            }
        }

        chain.doFilter(request, response);
    }
}
