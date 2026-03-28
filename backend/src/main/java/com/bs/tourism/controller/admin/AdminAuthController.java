package com.bs.tourism.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bs.tourism.common.Result;
import com.bs.tourism.entity.Admin;
import com.bs.tourism.entity.AdminOperationLog;
import com.bs.tourism.mapper.AdminMapper;
import com.bs.tourism.mapper.AdminOperationLogMapper;
import com.bs.tourism.util.JwtUtil;
import com.bs.tourism.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String username = params.get("username");
        String password = params.get("password");

        System.out.println("=== Login Debug ===");
        System.out.println("Username: " + username);
        System.out.println("Password length: " + (password != null ? password.length() : "null"));

        if (username == null || username.isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return Result.error(400, "密码不能为空");
        }

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(queryWrapper);

        System.out.println("Admin found: " + (admin != null));
        if (admin != null) {
            System.out.println("Admin ID: " + admin.getId());
            System.out.println("Admin username: " + admin.getUsername());
            System.out.println("Password hash in DB: " + admin.getPassword());
            System.out.println("Password matches: " + PasswordUtil.matches(password, admin.getPassword()));
        }

        if (admin == null) {
            return Result.error(400, "用户名或密码错误");
        }

        if (!PasswordUtil.matches(password, admin.getPassword())) {
            return Result.error(400, "用户名或密码错误");
        }

        String token = JwtUtil.createToken(admin.getId().intValue());

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", admin.getId());
        updateWrapper.set("last_login_time", new Date());
        adminMapper.update(null, updateWrapper);

        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(admin.getId());
        log.setModule("系统登录");
        log.setOperation("管理员登录");
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("admin", admin);

        return Result.success("登录成功", data);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getAdminInfo(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        if (admin == null) {
            return Result.error(401, "未登录");
        }

        List<String> roles = adminMapper.selectRolesByAdminId(admin.getId());
        List<String> permissions = adminMapper.selectPermissionsByAdminId(admin.getId());
        admin.setRoles(roles);
        admin.setPermissions(permissions);
        admin.setPassword(null);

        Map<String, Object> data = new HashMap<>();
        data.put("admin", admin);
        data.put("roles", roles);
        data.put("permissions", permissions);

        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        if (admin != null) {
            AdminOperationLog log = new AdminOperationLog();
            log.setAdminId(admin.getId());
            log.setModule("系统退出");
            log.setOperation("管理员退出登录");
            log.setIp(getClientIp(request));
            log.setCreateTime(new Date());
            operationLogMapper.insert(log);
        }
        return Result.success("退出成功");
    }

    @PostMapping("/updatePassword")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        if (admin == null) {
            return Result.error(401, "未登录");
        }

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return Result.error(400, "参数不完整");
        }

        if (!PasswordUtil.matches(oldPassword, admin.getPassword())) {
            return Result.error(400, "原密码错误");
        }

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", admin.getId());
        updateWrapper.set("password", PasswordUtil.encode(newPassword));
        updateWrapper.set("update_time", new Date());
        adminMapper.update(null, updateWrapper);

        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(admin.getId());
        log.setModule("系统设置");
        log.setOperation("修改密码");
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("密码修改成功");
    }

    @PostMapping("/updateProfile")
    public Result<Void> updateProfile(@RequestBody Admin adminInfo, HttpServletRequest request) {
        Admin admin = (Admin) request.getAttribute("currentAdmin");
        if (admin == null) {
            return Result.error(401, "未登录");
        }

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", admin.getId());
        if (adminInfo.getNickname() != null) {
            updateWrapper.set("nickname", adminInfo.getNickname());
        }
        if (adminInfo.getPhone() != null) {
            updateWrapper.set("phone", adminInfo.getPhone());
        }
        updateWrapper.set("update_time", new Date());
        adminMapper.update(null, updateWrapper);

        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(admin.getId());
        log.setModule("系统设置");
        log.setOperation("修改个人信息");
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("信息修改成功");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
