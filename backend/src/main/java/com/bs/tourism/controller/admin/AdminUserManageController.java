package com.bs.tourism.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bs.tourism.common.Result;
import com.bs.tourism.entity.*;
import com.bs.tourism.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/admin/manage/user")
public class AdminUserManageController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private AdminOperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Integer loginType) {

        Page<UserInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like("username", keyword)
                    .or().like("nickname", keyword)
                    .or().like("phone", keyword)
                    .or().like("wx_nickname", keyword));
        }

        if (gender != null) {
            queryWrapper.eq("gender", gender);
        }

        if (loginType != null) {
            queryWrapper.eq("login_type", loginType);
        }

        queryWrapper.orderByDesc("create_time");
        Page<UserInfo> result = userInfoMapper.selectPage(page, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<UserInfo> detail(@PathVariable Long id) {
        UserInfo user = userInfoMapper.selectById(id);
        if (user == null || user.getDeleteTime() != null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        UserInfo existUser = userInfoMapper.selectById(userInfo.getId());
        if (existUser == null || existUser.getDeleteTime() != null) {
            return Result.error(404, "用户不存在");
        }

        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userInfo.getId());
        if (userInfo.getNickname() != null) {
            updateWrapper.set("nickname", userInfo.getNickname());
        }
        if (userInfo.getPhone() != null) {
            updateWrapper.set("phone", userInfo.getPhone());
        }
        if (userInfo.getAge() != null) {
            updateWrapper.set("age", userInfo.getAge());
        }
        if (userInfo.getGender() != null) {
            updateWrapper.set("gender", userInfo.getGender());
        }
        if (userInfo.getIntro() != null) {
            updateWrapper.set("intro", userInfo.getIntro());
        }
        updateWrapper.set("update_time", new Date());
        userInfoMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("用户管理");
        log.setOperation("修改用户信息：" + existUser.getUsername());
        log.setTargetId(userInfo.getId());
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

        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("delete_time", new Date());
        userInfoMapper.update(null, updateWrapper);

        Admin currentAdmin = (Admin) request.getAttribute("currentAdmin");
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(currentAdmin.getId());
        log.setModule("用户管理");
        log.setOperation("删除用户：" + ids);
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
