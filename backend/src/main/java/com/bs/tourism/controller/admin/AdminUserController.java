package com.bs.tourism.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.common.Result;
import com.bs.tourism.entity.*;
import com.bs.tourism.mapper.*;
import com.bs.tourism.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<Admin> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like("username", keyword)
                    .or().like("nickname", keyword)
                    .or().like("phone", keyword));
        }

        queryWrapper.orderByDesc("create_time");
        Page<Admin> result = adminMapper.selectPage(page, queryWrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (Admin admin : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", admin.getId());
            item.put("username", admin.getUsername());
            item.put("nickname", admin.getNickname());
            item.put("phone", admin.getPhone());
            item.put("last_login_time", admin.getLastLoginTime());
            item.put("create_time", admin.getCreateTime());
            records.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null || admin.getDeleteTime() != null) {
            return Result.error(404, "管理员不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("phone", admin.getPhone());
        data.put("last_login_time", admin.getLastLoginTime());
        data.put("create_time", admin.getCreateTime());

        return Result.success(data);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String nickname = (String) params.get("nickname");
        String phone = (String) params.get("phone");

        if (!StringUtils.hasText(username)) {
            return Result.error(400, "用户名不能为空");
        }
        if (!StringUtils.hasText(password)) {
            return Result.error(400, "密码不能为空");
        }

        QueryWrapper<Admin> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("username", username);
        checkWrapper.isNull("delete_time");
        if (adminMapper.selectCount(checkWrapper) > 0) {
            return Result.error(400, "用户名已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(PasswordUtil.encode(password));
        admin.setNickname(nickname);
        admin.setPhone(phone);
        admin.setCreateTime(new Date());
        adminMapper.insert(admin);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("管理员管理");
        log.setOperation("新增管理员：" + username);
        log.setTargetId(admin.getId());
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long id = Long.valueOf(params.get("id").toString());
        String nickname = (String) params.get("nickname");
        String phone = (String) params.get("phone");
        String password = (String) params.get("password");

        Admin admin = adminMapper.selectById(id);
        if (admin == null || admin.getDeleteTime() != null) {
            return Result.error(404, "管理员不存在");
        }

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("nickname", nickname);
        updateWrapper.set("phone", phone);
        updateWrapper.set("update_time", new Date());

        if (StringUtils.hasText(password)) {
            updateWrapper.set("password", PasswordUtil.encode(password));
        }

        adminMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("管理员管理");
        log.setOperation("修改管理员：" + admin.getUsername());
        log.setTargetId(id);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("修改成功");
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Object idObj = params.get("id");
        List<Long> ids = new ArrayList<>();

        if (idObj instanceof List) {
            for (Object obj : (List<?>) idObj) {
                ids.add(Long.valueOf(obj.toString()));
            }
        } else {
            ids.add(Long.valueOf(idObj.toString()));
        }

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        if (ids.contains(currentAdmin.getId())) {
            return Result.error(400, "不能删除当前登录的管理员");
        }

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        adminMapper.update(null, updateWrapper);

        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("管理员管理");
        log.setOperation("删除管理员：" + ids);
        log.setIp(getClientIp(request));
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);

        return Result.success("删除成功");
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
