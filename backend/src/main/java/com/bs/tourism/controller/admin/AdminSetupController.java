package com.bs.tourism.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bs.tourism.entity.Admin;
import com.bs.tourism.mapper.AdminMapper;
import com.bs.tourism.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/setup")
public class AdminSetupController {

    @Autowired
    private AdminMapper adminMapper;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(queryWrapper);

        if (admin == null) {
            result.put("code", 404);
            result.put("msg", "管理员不存在");
            return result;
        }

        String encodedPassword = encoder.encode(newPassword);

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", admin.getId());
        updateWrapper.set("password", encodedPassword);
        adminMapper.update(null, updateWrapper);

        result.put("code", 200);
        result.put("msg", "密码重置成功");
        result.put("data", Map.of(
                "username", username,
                "newPassword", newPassword,
                "newHash", encodedPassword,
                "verifyMatch", encoder.matches(newPassword, encodedPassword)));

        return result;
    }

    @GetMapping("/test-password")
    public Map<String, Object> testPassword(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(queryWrapper);

        if (admin == null) {
            result.put("code", 404);
            result.put("msg", "管理员不存在");
            return result;
        }

        String dbHash = admin.getPassword();
        boolean matches = encoder.matches(password, dbHash);

        String newHash = encoder.encode(password);
        boolean newHashMatches = encoder.matches(password, newHash);

        result.put("code", 200);
        result.put("data", Map.of(
                "username", username,
                "inputPassword", password,
                "dbHash", dbHash,
                "dbHashMatches", matches,
                "newGeneratedHash", newHash,
                "newHashMatches", newHashMatches));

        return result;
    }
}
