package com.bs.tourism.interceptor;

import com.bs.tourism.entity.Admin;
import com.bs.tourism.mapper.AdminMapper;
import com.bs.tourism.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();

        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        if (token == null || token.isEmpty()) {
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null) {
                String[] cookies = cookieHeader.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.trim().split("=", 2);
                    if (parts.length == 2 && "admin_token".equals(parts[0])) {
                        token = parts[1];
                        break;
                    }
                }
            }
        }

        if (token == null || token.isEmpty()) {
            if (requestURI.contains("/admin/page")) {
                response.sendRedirect("/api/admin/page/login");
                return false;
            }
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Integer adminId = JwtUtil.getUserId(token);

        if (adminId == null) {
            if (requestURI.contains("/admin/page")) {
                response.sendRedirect("/api/admin/page/login");
                return false;
            }
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"token无效\"}");
            return false;
        }

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null || admin.getDeleteTime() != null) {
            if (requestURI.contains("/admin/page")) {
                response.sendRedirect("/api/admin/page/login");
                return false;
            }
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"管理员不存在\"}");
            return false;
        }

        request.setAttribute("currentAdmin", admin);

        return true;
    }
}
